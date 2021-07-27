package edu.neu.venuify;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class SearchActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public int getContentViewId() { return R.layout.activity_search; }

    @Override
    public int getNavigationMenuItemId() { return R.id.nav_bar_search; }



    public void EnterSearchQuery(View view) {
        startActivity(new Intent(this, EnterSearchQuery.class));
    }


}
