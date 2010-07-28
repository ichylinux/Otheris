package jp.co.hybitz.otheris;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class OtherisActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initAction();
    }
    
    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
    }
    
    private void initAction() {
    	final MainView view = (MainView) findViewById(R.id.main_view);
    	
    	Button left = (Button) findViewById(R.id.left);
    	left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				view.moveLeft();
			}
		});
    	
    	Button rotate = (Button) findViewById(R.id.right);
    	rotate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				view.rotate();
			}
		});

    	Button right = (Button) findViewById(R.id.right);
    	right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				view.moveRight();
			}
		});
    }
}