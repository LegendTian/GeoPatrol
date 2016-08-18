package com.al.app.geopatrol.utils;

import android.app.Activity;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 各种字典
 *
 * @author txy
 * @version 2016年6月18日  下午11:50:49
 */
public class PatrolUtils {
	public static List<String> levelList= new ArrayList<String>(){};
	public static Map<String, String> troubleType = Maps.newHashMap();
	public static List<String> ttList= new ArrayList<String>(){};
	public static void init(){
		levelList.add("正常");
		levelList.add("一级");
		levelList.add("二级");
		levelList.add("三级");

		troubleType.put("A", "第三方施工");
		troubleType.put("B", "违章占压");
		troubleType.put("C", "塌陷");
		troubleType.put("D", "水工保护");
		troubleType.put("E", "地面标识");
		troubleType.put("F", "采空区");
		troubleType.put("G", "阀室");
		troubleType.put("H", "其他");

		if(ttList.size()==0) {
			ttList.add("第三方施工");
			ttList.add("违章占压");
			ttList.add("塌陷");
			ttList.add("水工保护");
			ttList.add("地面标识");
			ttList.add("采空区");
			ttList.add("阀室");
			ttList.add("其他");
		}

	}

	
}
