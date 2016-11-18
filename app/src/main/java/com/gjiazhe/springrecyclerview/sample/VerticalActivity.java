package com.gjiazhe.springrecyclerview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gjiazhe.springrecyclerview.SpringRecyclerView;

public class VerticalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical);

        SpringRecyclerView springRecyclerView = (SpringRecyclerView) findViewById(R.id.spring_recycler_view);
        springRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        springRecyclerView.setAdapter(new MyAdapter());
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(VerticalActivity.this);
            View view = inflater.inflate(R.layout.item1, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.textView.setText("Item " + (position + 1));
        }

        @Override
        public int getItemCount() {
            return 20;
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
