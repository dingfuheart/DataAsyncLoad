package cn.com.dataasyncload.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.dataasyncload.Service.ContactService;
import cn.com.dataasyncload.domain.Contact;

import com.example.dataasyncload.R;

public class ContactAdapter extends BaseAdapter {
	private List<Contact> data;
	private int listviewItem;
	private File cache;
	/**
	 * LayoutInflater的作用类似于
	 * findViewById(),不同点是LayoutInflater是用来找layout文件夹下的xml布局文件，并且实例化！ 而
	 * findViewById()是找具体某一个xml下的具体 widget控件(如:Button,TextView等)。
	 */
	private LayoutInflater layoutInflater;

	public ContactAdapter(Context context, List<Contact> data,
			int listviewItem, File cache) {
		this.data = data;
		this.listviewItem = listviewItem;
		this.cache = cache;
		this.layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);// 取得xml里定义的view
		/***
		 * getSystemService()是Android很重要的一个API，它是Activity的一个方法，
		 * 根据传入的NAME来取得对应的Object，然后转换成相应的服务对象。以下介绍系统相应的服务。 传入的Name 返回的对象 说明
		 * WINDOW_SERVICE WindowManager 管理打开的窗口程序 LAYOUT_INFLATER_SERVICE
		 * LayoutInflater 取得xml里定义的view ACTIVITY_SERVICE ActivityManager
		 * 管理应用程序的系统状态 POWER_SERVICE PowerManger 电源的服务 ALARM_SERVICE
		 * AlarmManager 闹钟的服务 NOTIFICATION_SERVICE NotificationManager 状态栏的服务
		 * KEYGUARD_SERVICE KeyguardManager 键盘锁的服务 LOCATION_SERVICE
		 * LocationManager 位置的服务，如GPS SEARCH_SERVICE SearchManager 搜索的服务
		 * VEBRATOR_SERVICE Vebrator 手机震动的服务 CONNECTIVITY_SERVICE Connectivity
		 * 网络连接的服务 WIFI_SERVICE WifiManager Wi-Fi服务 TELEPHONY_SERVICE
		 * TeleponyManager 电话服务
		 */
	}

	/** 得到数据的总数 */
	@Override
	public int getCount() {
		return data.size();
	}

	/** 根据数据索引，得到集合中所对应的数据 */
	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = null;
		TextView textView = null;

		if (convertView == null) {
			convertView = layoutInflater.inflate(listviewItem, null);
			imageView = (ImageView) convertView.findViewById(R.id.imageView);
			textView = (TextView) convertView.findViewById(R.id.textView);
			convertView.setTag(new DataWrapper(imageView, textView));// 将内容包装起来以备以后使用
		} else {
			DataWrapper dataWrapper = (DataWrapper) convertView.getTag();// 将包装类取出来
			// 从包装类中取数据
			imageView = dataWrapper.getImageView();
			textView = dataWrapper.getTextView();
		}
		Contact contact = data.get(position);
		textView.setText(contact.getDate());
		/** 异步加载图片文件 */
		asynchImageLoad(imageView, contact.getNightPictureUrl());
		return convertView;
	}

	/*
	 * //该方法会创建很多的线程，也会很耗资源 private void asynchImageLoad(final ImageView
	 * imageView, final String imagePath) { final Handler handler=new Handler(){
	 * 
	 * @Override public void handleMessage(Message msg) {//运行在主线程中 Uri uri=(Uri)
	 * msg.obj; if (uri!=null&&imageView!=null) { imageView.setImageURI(uri); }
	 * } }; Runnable runnable=new Runnable() {
	 * 
	 * @Override public void run() { try { Uri
	 * uri=ContactService.getImage(imagePath, cache);
	 * handler.sendMessage(handler.obtainMessage(10,uri)); } catch (Exception e)
	 * { e.printStackTrace(); } } }; new Thread(runnable).start(); }
	 */

	/** 异步加载图片文件 */
	private void asynchImageLoad(ImageView imageView, String imagePath) {
		AsycImageTask asycImageTask = new AsycImageTask(imageView);
		asycImageTask.execute(imagePath);
	}

	/**
	 * 使用AsyncTask提高性能 可选方法： 1， onprogressupdate(progress…)
	 * 可以使用进度条增加用户体验度。此方法在主线程执行，用户显示任务执行的进度。 2， onpreExecute()
	 * 这里是最新用户调用excute时的接口，当任务执行之前开始调用此方法，可以在这里显示进度对话框。 3， onCancelled()
	 * 用户调用取消时，要做的操作。
	 * 
	 * AsyncTask<Params, Progress, Result>
	 * AsyscTask定义了三种泛型类型params,progress和result. 
	 * 1，params启动任务执行的输入参数，比如http请求的URL
	 * 2， progress后台任务执行的百分比 
	 * 3，result后台执行任务最终返回的结果，比如String，比如我需要得到的list。
	 * 
	 * 使用AsyncTask类，遵守的准则：
	 * 1， Task的实例必须在UI thread中创建；
	 * 2， Execute方法必须在UI thread中调用
	 * 3， 不要手动的调用onPfreexecute()，onPostExecute(result)Doinbackground(params…),onProgressupdate(progress…)这几个方法；
	 * 4， 该task只能被执行一次，否则多次调用时将会出现异常;
	 * AsyncTask的整个调用过程都是从execute方法开始的，一旦在主线程中调用execute方法，就可以通过onpreExecute方法，
	 * 这是一个预处理方法，比如可以在这里开始一个进度框，同样也可以通过onprogressupdate方法给用户一个进度条的显示，增加用户体验；
	 * 最后通过onpostexecute方法，相当于handler处理UI的方式，在这里可以使用在doinbackground得到的结果处理操作UI。
	 * 此方法在主线程执行，任务执行的结果作为此方法的参数返回
	 */
	private final class AsycImageTask extends AsyncTask<String, Integer, Uri> {
		private ImageView imageView;

		public AsycImageTask(ImageView imageView) {
			this.imageView = imageView;
		}

		/**
		 * 后台执行，比较耗时的操作都可以放在这里。 注意这里不能直接操作UI。此方法在后台线程执行，完成任务的主要工作
		 * ，通常需要较长的时间。在执行过程中可以调用 Public progress(progress…)来更新任务的进度。
		 */
		@Override
		protected Uri doInBackground(String... params) {// 子线程中执行
			try {
				return ContactService.getImage(params[0], cache);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * 相当于handler处理UI的方式，在这里可以使用在doinbackground得到的结果
		 * 处理操作UI。此方法在主线程执行，任务执行的结果作为此方法的参数返回。
		 */
		@Override
		protected void onPostExecute(Uri result) {// 运行在主线程
			if (result != null && imageView != null) {
				imageView.setImageURI(result);
			}
		}

	}

	/** 数据包装类 */
	private final class DataWrapper {
		private ImageView imageView;
		private TextView textView;

		public ImageView getImageView() {
			return imageView;
		}

		public TextView getTextView() {
			return textView;
		}

		public DataWrapper(ImageView imageView, TextView textView) {
			this.imageView = imageView;
			this.textView = textView;
		}
	}

}
