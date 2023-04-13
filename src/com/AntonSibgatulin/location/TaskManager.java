package com.AntonSibgatulin.location;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class TaskManager {

	public ArrayList<TaskModel> task_list = new ArrayList<>();
	public HashMap<String, ArrayList> hashMap= new HashMap<>();
	
	public TaskManager(){}
	
	public TaskManager(String paths) {
		File[] files = new File(paths).listFiles();
		for(int i =0;i<files.length;i++){
			File file = files[i];
			String all = "";
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				String piece  = null;
				while((piece=bufferedReader.readLine())!=null){
					all+=piece;
				}
			
			
			
			
				bufferedReader.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject jsonObject = new JSONObject(all);
			JSONArray jsonArray = jsonObject.getJSONArray("type");
			String id = jsonObject.getString("id");
			 ArrayList<TaskModel> task_list = new ArrayList<>();
			
			for(int j = 0;j<jsonArray.length();j++){
				JSONObject jsonObject2 = jsonArray.getJSONObject(j);
				jsonObject2.put("id", id+"_"+j);
				TaskModel taskModel = new TaskModel(jsonObject2);
				
				task_list.add(taskModel);
				this.task_list.add(taskModel);
				hashMap.put(id,task_list);
				//System.out.println("Task is loding "+id+"_"+j);
			}
		}
		
		
	}
	
}
