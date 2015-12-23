package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by Luis on 12/23/2015.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetListService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            private Cursor data = null;

            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                Date fragmentdate = new Date(System.currentTimeMillis()+((-2)*(86400000)));
                SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
                String date = mformat.format(fragmentdate);
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                        null,
                        null,
                        new String[]{date},
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                System.out.println("marco " + data.getCount());
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {


                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);

                String home = data.getString(data.getColumnIndex(DatabaseContract.scores_table.HOME_COL));
                String away = data.getString(data.getColumnIndex(DatabaseContract.scores_table.AWAY_COL));
                String league = data.getString(data.getColumnIndex(DatabaseContract.scores_table.LEAGUE_COL));
                String date = data.getString(data.getColumnIndex(DatabaseContract.scores_table.DATE_COL));
                String time = data.getString(data.getColumnIndex(DatabaseContract.scores_table.TIME_COL));
                String homeGoals = data.getString(data.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL));
                String awayGoals = data.getString(data.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL));

                System.out.println("Aqui" + home + " " + away + " " + league + " " + date + " " + time + " " + homeGoals + " " + awayGoals);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    String formatedTime;
                    String winner = "";
                    if (time.substring(time.length() - 2, time.length()).equals("00")) {
                        formatedTime = time.substring(0, time.length() - 3);
                    } else {
                        formatedTime = time.substring(0, time.length() - 3) + getApplicationContext().getString(R.string.half_hour);
                    }
                    if (Integer.parseInt(homeGoals) > Integer.parseInt(awayGoals)) {
                        winner = home;
                    } else if (Integer.parseInt(homeGoals) < Integer.parseInt(awayGoals)) {
                        winner = away;
                    }
                    setRemoteContentDescription(views, home + getApplicationContext().getString(R.string.versus)
                            + away + getApplicationContext().getString(R.string.hour)
                            + formatedTime + getApplicationContext().getString(R.string.score)
                            + " " + homeGoals + getApplicationContext().getString(R.string.to) + " "
                            + awayGoals
                            + winner);
                }
                views.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(home));
                views.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(away));
                views.setTextViewText(R.id.score_textview, Utilies.getScores(Integer.parseInt(homeGoals), Integer.parseInt(awayGoals)));
                views.setTextViewText(R.id.home_name, home);
                views.setTextViewText(R.id.away_name, away);
                views.setTextViewText(R.id.data_textview, time);
//                final Intent fillInIntent = new Intent();
//                String locationSetting =
//                        Utility.getPreferredLocation(DetailWidgetRemoteViewsService.this);
//                Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
//                        locationSetting,
//                        dateInMillis);
//                fillInIntent.setData(PopularMoviesContract.PopularEntry.CONTENT_URI);
//                byte[] byteArray = PopularMoviesUtility.convertImageToBytes(posters[position]);
//                fillInIntent.putStringArrayListExtra(PopularMoviesDetailActivityFragment.MOVIES_LIST_NAME, moviesInfo);
//                fillInIntent.putExtra(PopularMoviesDetailActivityFragment.PREFERENCE_NAME, preference);
//                fillInIntent.putExtra(PopularMoviesDetailActivityFragment.BYTE_ARRAY_NAME, byteArray);
//                views.setOnClickFillInIntent(R.id.widget_grid_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.list_item_layout, description);
    }
}
