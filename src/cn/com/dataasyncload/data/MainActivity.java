package cn.com.dataasyncload.data;

import java.io.File;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.ListView;
import cn.com.dataasyncload.Service.ContactService;
import cn.com.dataasyncload.adapter.ContactAdapter;
import cn.com.dataasyncload.domain.Contact;
import com.example.dataasyncload.R;

public class MainActivity extends Activity {
	
	private ListView listView;  
    /**缓存文件*/  
    private File cache;  
      
    /**接受消息,处理消息 ,此Handler会与当前主线程一块运行 
     * 使用匿名内部类来复写Handler当中的handlerMessage()方法  */  
    @SuppressLint("HandlerLeak") 
    Handler handler = new Handler() {  
        // 接受数据  
        @SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {  
            //设置适配器，将获取的数据使用适配器更新View  
            listView.setAdapter(new ContactAdapter(MainActivity.this,  
                    (List<Contact>) msg.obj, R.layout.listview_item, cache));  
        };  
    };  
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.main);  
        listView = (ListView) this.findViewById(R.id.listView);  
        /**在SD卡中生成缓存目录*/  
        cache = new File(Environment.getExternalStorageDirectory(), "cache");  
        /**如果目录不存在就新建一个*/  
        if (!cache.exists())  cache.mkdir();  
              
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
                try {  
                    //获取联系人数据  
                    List<Contact> data= ContactService.getContacts();  
                    // 向Handler发送消息,更新UI  
                    handler.sendMessage(handler.obtainMessage(22, data));  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }).start();  
    }  
      
    @Override  
    protected void onDestroy() {  
        /**清除缓存文件*/  
        for (File file:cache.listFiles()) {  
            file.delete();  
        }  
        cache.delete();  
        super.onDestroy();  
    }  

}
