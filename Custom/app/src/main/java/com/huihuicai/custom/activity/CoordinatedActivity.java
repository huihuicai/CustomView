package com.huihuicai.custom.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huihuicai.custom.R;

public class CoordinatedActivity extends AppCompatActivity {

    private RecyclerView rvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinated);
        rvList = (RecyclerView) findViewById(R.id.rv_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvList.setLayoutManager(manager);
        rvList.setAdapter(new RvAdapter(this));
    }

    public class RvAdapter extends RecyclerView.Adapter<RvAdapter.Holder> {

        private LayoutInflater mInflater;

        public RvAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_data, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 20;
        }

        class Holder extends RecyclerView.ViewHolder {

            Holder(View itemView) {
                super(itemView);
            }
        }
    }
}
