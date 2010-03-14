package jp.co.techfirm.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ListActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        
        Button create = (Button)findViewById(R.id.Button01);
        create.setOnClickListener(new BaseSelectOnClickLister());
        
        Button detail = (Button)findViewById(R.id.Button02);
        detail.setOnClickListener(new DetailOnClickLister());
    }
    
    class DetailOnClickLister implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClassName(getPackageName(), DetailActivity.class.getName());
			startActivity(intent);
		}
    	
    }
    
    class BaseSelectOnClickLister implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClassName(getPackageName(), BaseSelectActivity.class.getName());
			startActivity(intent);
		}
    	
    }
    
    
}