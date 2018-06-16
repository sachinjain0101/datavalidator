package com.bullhorn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Misc {

	public static void main(String args[]) throws IOException {
		String sample = "";
		sample = "[{\"EmployeeFirstName\":\"Sachin\",\"EmployeeLastName\":\"Jain\",\"EmployeeID\":\"1234\",\"EmployeeSSN\":\"987654321\"}]";
		sample = "[{\"EmployeeFirstName\":\"Sachin\",\"EmployeeLastName\":\"Jain\",\"EmployeeID\":\"1234\",\"EmployeeSSN\":\"987654321\",\"Codes\":{\"X1\":\"Y1\",\"X2\":\"Y2\"}},{\"EmployeeFirstName\":\"Shalina\",\"EmployeeLastName\":\"Jain\",\"EmployeeID\":\"5678\",\"EmployeeSSN\":\"98989898\",\"Codes\":{\"X1\":\"Y1\"}}]";
		//Gson gson = new Gson();

		//Test[] emp1 = gson.fromJson(sample, Test[].class);

		JsonParser parser = new JsonParser();
		JsonElement tree = parser.parse(sample);

		JsonArray arr = tree.getAsJsonArray();

		System.out.println(arr.size());

		List<Map<String, String>> assignmentList = new ArrayList<Map<String,String>>();
		
		arr.forEach((element) -> {
			JsonObject o = element.getAsJsonObject();
			Set<String> keys = o.keySet();
			Map<String,String> kvMap = new HashMap<String,String>();
			keys.forEach((k) -> {
				System.out.println("Key: " + k + "\t\tValue: " + o.get(k).toString());
				kvMap.put(k, o.get(k).toString());
			});
			assignmentList.add(kvMap);
		});

		System.out.println("\n=============");
		
		assignmentList.forEach((v)->{
			System.out.println(v.toString() + "\n=============");
		});
		
		System.out.println("--");

	}

}
