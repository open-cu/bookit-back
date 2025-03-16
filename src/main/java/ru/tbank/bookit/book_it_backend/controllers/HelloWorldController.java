package ru.tbank.bookit.book_it_backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("say")
public class HelloWorldController {
	@GetMapping(value = "hello",produces= "text/html")
	public String sayHello () {
		return "<!DOCTYPE html>"+
			"<html>"+
			"	<head><title>Hello world!</title></head>"+
			"	<body>Hello world!</body>"+
			"</html>";
	}
} 