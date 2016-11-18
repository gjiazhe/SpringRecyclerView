package com.gjiazhe.springrecyclerview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gjiazhe.springrecyclerview.SpringRecyclerView;

public class StaggeredGridLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staggered_grid_layout);
        SpringRecyclerView springRecyclerView = (SpringRecyclerView) findViewById(R.id.spring_recycler_view);
        springRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        springRecyclerView.setAdapter(new MyAdapter());
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private int[] mRandomHeight = new int[25];

        MyAdapter() {
            for (int i=0; i<mRandomHeight.length; i++) {
                mRandomHeight[i] = (int) (100 + Math.random() * 400);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(StaggeredGridLayoutActivity.this);
            View view = inflater.inflate(R.layout.item2, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.textView.setText(position + 1 + "");
            ViewGroup.LayoutParams lp = holder.textView.getLayoutParams();
            lp.height = mRandomHeight[position];
            holder.textView.setLayoutParams(lp);
        }

        @Override
        public int getItemCount() {
            return mRandomHeight.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            MyViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.text1);
            }
        }
    }
}
