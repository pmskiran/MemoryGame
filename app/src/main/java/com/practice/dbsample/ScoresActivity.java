package com.practice.dbsample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by spartans on 26/4/16.
 */
public class ScoresActivity extends AppCompatActivity {

    ListView mListView;
    ArrayList<String> mUserData;
    ArrayList<Integer> mDistinctScores;
    private static LayoutInflater mInflater =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scores);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.score_listView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DBHelper dbHelper = new DBHelper(this);
        mUserData = dbHelper.getRecords();
        mDistinctScores = dbHelper.getDistinctScores();
        mListView.setAdapter(new CustomAdapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_game) {
            Intent intent = new Intent(ScoresActivity.this, MainActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class CustomAdapter extends BaseAdapter {

        int tempRank, tempScore;

        public CustomAdapter() {
            mInflater = ( LayoutInflater )ScoresActivity.this.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            if(mUserData != null)
                return mUserData.size();
            else
                return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder holder;

            if(convertView == null) {
                view = mInflater.inflate(R.layout.scores_row, null);
                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.scores_row_name_tv);
                holder.score = (TextView) view.findViewById(R.id.scores_row_score_tv);
                holder.rank = (TextView) view.findViewById(R.id.scores_row_rank_tv);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String data[] = mUserData.get(position).toString().split(",");
            holder.name.setText(data[0]);
            holder.score.setText(data[1]);
            holder.rank.setText(""+getRank(Integer.parseInt(data[1])));

            return view;
        }

        public class ViewHolder {
            TextView name;
            TextView score;
            TextView rank;
        }

        private int getRank(int score) {
            int rank = -1;
            int size = mDistinctScores.size();
            int i;
            for(i=0; i<size; i++) {
                if(score >= mDistinctScores.get(i)) {
                    rank = i+1;
                    break;
                }
            }
            if(rank == -1)
                rank = i;

            Log.i(getClass().getSimpleName(),"Rank :: "+rank);
            return rank;
        }
    }

}
