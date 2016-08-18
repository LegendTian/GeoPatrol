package com.al.app.geopatrol.activitys;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.al.app.geopatrol.R;
import com.al.app.geopatrol.adapter.AlbumGridViewAdapter;
import com.al.app.geopatrol.utils.AlbumHelper;
import com.al.app.geopatrol.utils.Bimp;
import com.al.app.geopatrol.utils.ImageBucket;
import com.al.app.geopatrol.utils.ImageItem;
import com.al.app.geopatrol.utils.PublicWay;
import com.al.app.geopatrol.utils.Res;


/**
 * 这个是进入相册显示所有图片的界面
 * 
 * @author txy
 * @version 2016/04/13  下午11:47:15
 */
public class AlbumActivity extends Activity {
	//显示手机里的�?有图片的列表控件
	private GridView gridView;
	//当手机里没有图片时，提示用户没有图片的控�?
	private TextView tv;
	//gridView的adapter
	private AlbumGridViewAdapter gridImageAdapter;
	//完成按钮
	private Button okButton;
	// 返回按钮
	private Button back;
	// 取消按钮
	private Button cancel;
	private Intent intent;
	// 预览按钮
	private Button preview;
	private Context mContext;
	private ArrayList<ImageItem> dataList;
	private AlbumHelper helper;
	public static List<ImageBucket> contentList;
	public static Bitmap bitmap;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_album);
		PublicWay.activityList.add(this);
		mContext = this;
		//注册�?个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消�?�中的图片仍处于选中状�??
		IntentFilter filter = new IntentFilter("data.broadcast.action");  
		registerReceiver(broadcastReceiver, filter);  
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.plugin_camera_no_pictures);
        init();
		initListener();
		//这个函数主要用来控制预览和完成按钮的状�??
		isShowOkBt();
	}
	
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {  
		  
        @Override  
        public void onReceive(Context context, Intent intent) {  
        	//mContext.unregisterReceiver(this);
            // TODO Auto-generated method stub  
        	gridImageAdapter.notifyDataSetChanged();
        }  
    };  

	// 预览按钮的监听
	private class PreviewListener implements OnClickListener {
		public void onClick(View v) {
			if (Bimp.tempSelectBitmap.size() > 0) {
				intent.putExtra("position", "1");
				intent.setClass(AlbumActivity.this, GalleryActivity.class);
				startActivity(intent);
			}
		}

	}

	// 完成按钮的监听
	private class AlbumSendListener implements OnClickListener {
		public void onClick(View v) {
			overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println(mContext);

			startActivity(setClassToIntent(intent));
			finish();
		}

	}

	// 返回按钮监听
	private class BackListener implements OnClickListener {
		public void onClick(View v) {
			intent.setClass(AlbumActivity.this, ImageFile.class);
			startActivity(intent);
		}
	}

	// 取消按钮的监�?
	private class CancelListener implements OnClickListener {
		public void onClick(View v) {
			Bimp.tempSelectBitmap.clear();
			//intent.setClass(mContext, PostPatrolActivity.class);
			startActivity(setClassToIntent(intent));
		}
	}

	private Intent setClassToIntent(Intent intent){
		String activityName=getIntent().getStringExtra("activityName");
		switch (activityName){
			case "TrackRiskActivity":
				intent.setClass(mContext,TrackRiskActivity.class);
				break;
			case "PostPatrolActivity":
				intent.setClass(mContext,PostPatrolActivity.class);
				break;
			case "PostRiskActivity":
				intent.setClass(mContext,PostRiskActivity.class);
				break;
			default:
				intent.setClass(mContext,PostPatrolActivity.class);
				break;
		}
		return intent;
	}

	// 初始化，给一些对象赋值
	private void init() {
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		
		contentList = helper.getImagesBucketList(false);
		dataList = new ArrayList<ImageItem>();
		for(int i = 0; i<contentList.size(); i++){
			dataList.addAll( contentList.get(i).imageList );
		}
		
		back = (Button) findViewById(R.id.back);
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(new CancelListener());
		back.setOnClickListener(new BackListener());
		preview = (Button) findViewById(R.id.preview);
		preview.setOnClickListener(new PreviewListener());
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		gridView = (GridView) findViewById(R.id.myGrid);
		gridImageAdapter = new AlbumGridViewAdapter(this,dataList,
				Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);
		tv = (TextView) findViewById(R.id.myText);
		gridView.setEmptyView(tv);
		okButton = (Button) findViewById(R.id.ok_button);
		okButton.setText(getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size()
				+ "/"+PublicWay.num+")");
	}

	private void initListener() {

		gridImageAdapter
				.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(final ToggleButton toggleButton,
							int position, boolean isChecked,Button chooseBt) {
						if (Bimp.tempSelectBitmap.size() >= PublicWay.num) {
							toggleButton.setChecked(false);
							chooseBt.setVisibility(View.GONE);
							if (!removeOneData(dataList.get(position))) {
								Toast.makeText(AlbumActivity.this, getString(R.string.only_choose_num),
										Toast.LENGTH_LONG).show();
							}
							return;
						}
						if (isChecked) {
							chooseBt.setVisibility(View.VISIBLE);
							Bimp.tempSelectBitmap.add(dataList.get(position));
							okButton.setText(getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size()
									+ "/"+PublicWay.num+")");
						} else {
							Bimp.tempSelectBitmap.remove(dataList.get(position));
							chooseBt.setVisibility(View.GONE);
							okButton.setText(getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
						}
						isShowOkBt();
					}
				});

		okButton.setOnClickListener(new AlbumSendListener());

	}

	private boolean removeOneData(ImageItem imageItem) {
			if (Bimp.tempSelectBitmap.contains(imageItem)) {
				Bimp.tempSelectBitmap.remove(imageItem);
				okButton.setText(getString(R.string.finish)+"(" +Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
				return true;
			}
		return false;
	}
	
	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			okButton.setText(getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
			preview.setPressed(true);
			okButton.setPressed(true);
			preview.setClickable(true);
			okButton.setClickable(true);
			okButton.setTextColor(Color.WHITE);
			preview.setTextColor(Color.WHITE);
		} else {
			okButton.setText(getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
			preview.setPressed(false);
			preview.setClickable(false);
			okButton.setPressed(false);
			okButton.setClickable(false);
			okButton.setTextColor(Color.parseColor("#E1E0DE"));
			preview.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			intent.setClass(AlbumActivity.this, ImageFile.class);
			startActivity(intent);
		}
		return false;

	}
@Override
protected void onRestart() {
	isShowOkBt();
	super.onRestart();
}
}
