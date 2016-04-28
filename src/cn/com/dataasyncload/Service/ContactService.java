package cn.com.dataasyncload.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;


import android.net.Uri;
import android.util.Xml;
import cn.com.dataasyncload.domain.Contact;
import cn.com.dataasyncload.utils.MD5;

public class ContactService {
	
	
	/** 
     * 获取联系人数据 
     *  
     * @return 
     * @throws Exception 
     */  
    public static List<Contact> getContacts() throws Exception {  
        String path = "http://api.map.baidu.com/telematics/v3/weather?location=%E5%8C%97%E4%BA%AC&output=xml&ak=6tYzTvGZSOpYB5Oc2YGGOKt8";  
        HttpURLConnection connection = (HttpURLConnection) new URL(path)  
                .openConnection();  
        connection.setConnectTimeout(5000);  
        connection.setRequestMethod("GET");  
        if (connection.getResponseCode() == 200) {  
            return parseXML(connection.getInputStream());  
        }  
        return null;  
    }  
    /**转化XML获取数据 
     * 服务器端的xml文件如下。。。。。。 
     * <?xml version="1.0" encoding="UTF-8"?> 
        <contacts> 
            <contact id="1"> 
                <name>Roco_1</name> 
                <image src="http://192.168.1.100:8080/Hello/images/1.png" /> 
            </contact> 
            ....... 
        </contacts>*/  
    private static List<Contact> parseXML(InputStream inputStream)  
            throws Exception {  
        List<Contact> contacts = null ; 
        Contact contact = null;  
        String nodeName = null;// 当前元素节点名称
        int dateFlag = 0;
        
        XmlPullParser xmlPullParser = Xml.newPullParser();  
        xmlPullParser.setInput(inputStream, "UTF-8");  
        int eventType = xmlPullParser.getEventType();  
        while (eventType != XmlPullParser.END_DOCUMENT) {// 如果文档没有结束
			nodeName = xmlPullParser.getName();// 取得当前读取的节点名称
			switch (eventType) {
			// ...文档开始
			case XmlPullParser.START_DOCUMENT:
				System.out.println("文档开始");
				contacts = new ArrayList<Contact>();
				break;
			// ...开始读取标记
			case XmlPullParser.START_TAG:
				if ("date".equals(nodeName)) {
					if (dateFlag == 0) {// 表示第一个date节点，无视 之~~~
						dateFlag++;
					} else {
						contact = new Contact();
						String temp = xmlPullParser.nextText();
						contact.setDate(temp);
					}
				} else if ("dayPictureUrl".equals(nodeName)) {
					// ...白天的图片
					String temp = xmlPullParser.nextText();
					contact.setDayPictureUrl(temp);
				} else if ("nightPictureUrl".equals(nodeName)) {
					// ...晚上的图片
					String temp = xmlPullParser.nextText();
					contact.setNightPictureUrl(temp);
					contacts.add(contact);
				}
				break;
			// ...结束标记
			case XmlPullParser.END_TAG:
				nodeName = xmlPullParser.getName();
				if ("weather_data".equals(nodeName)) {
					contact = null;// 清空对象
				}
				break;
			default:
				break;
			}
			eventType = xmlPullParser.next();// 将操作事件指到下一个标记
		}
        return contacts;  
    }  
  
    /** 
     * 获取网络图片,如果图片存在于缓存中，就返回该图片，否则从网络中加载该图片并缓存起来 
     *  
     * @param path 
     *            图片路径 
     * @return 
     */  
    public static Uri getImage(String imagePath, File cacheDir)  
            throws Exception {  
        //缓存文件的文件名用MD5进行加密  
        File localFile = new File(cacheDir, MD5.getMD5(imagePath)  
                + imagePath.substring(imagePath.lastIndexOf(".")));   
        if (localFile.exists()) {  
            return Uri.fromFile(localFile);  
        } else {  
            HttpURLConnection connection = (HttpURLConnection) new URL(  
                    imagePath).openConnection();  
            connection.setConnectTimeout(5000);  
            connection.setRequestMethod("GET");  
            //将文件缓存起来  
            if (connection.getResponseCode() == 200) {  
                FileOutputStream outputStream = new FileOutputStream(localFile);  
                InputStream inputStream = connection.getInputStream();  
                byte[] buffer = new byte[1024];  
                int len = 0;  
                while ((len = inputStream.read(buffer)) != -1) {  
                    outputStream.write(buffer, 0, len);  
                }  
                inputStream.close();  
                outputStream.close();  
                return Uri.fromFile(localFile);  
            }  
        }  
        return null;  
    }  
}
