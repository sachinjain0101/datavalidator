package com.bullhorn.rest;

import com.bullhorn.app.Constants;
import com.bullhorn.app.OperaStatus;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Api(value = "Base resource for Opera-DataValidator")
@RequestMapping("/dataValidator")
public class DataValidator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataValidator.class);

	@ApiOperation(value="Test to see Data Validator is working or not.")
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test() {
		return "Opera Data Validator is running...";
	}

	@ApiOperation(value="Test to see Data Validator is working or not.")
	@RequestMapping(value = "/statusDesctiption", method = RequestMethod.GET)
	public ResponseEntity<String> statusDesctiption() {
		Gson gson = new Gson();
		return new ResponseEntity(OperaStatus.getLst(),HttpStatus.OK);
	}

    @ApiOperation(value="Gets the Data Validator thread information.")
    @RequestMapping(value = "/threads", method = RequestMethod.GET)
    public List<String> threads(){
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
        List<String> lst = new ArrayList<>();
        for(Thread t:threadArray){
            lst.add(t.getName()+" : "+t.getState().toString());
        }
        return lst.stream().filter((s)->s.startsWith(Constants.DATA_VALIDATOR)).collect(Collectors.toList());
    }

}

/*
    {
        "client": "SOME",
        "data": [{
            "EmployeeFirstName": "Sachin WhatUp",
            "EmployeeLastName": "Jain",
            "EmployeeID": "1234",
            "EmployeeSSN": "987654321",
            "Codes": {
                "X1": "Y1",
                "X2": "Y2"
            }
        }, {
            "EmployeeFirstName": "Shalina",
            "EmployeeLastName": "Jain",
            "EmployeeID": "",
            "EmployeeSSN": "98989898",
            "Codes": {
                "X1": "Y1"
            }
        }],
        "integrationKey": "12345",
        "mapName": "Test",
        "messageId": "67890"
    }
*/

