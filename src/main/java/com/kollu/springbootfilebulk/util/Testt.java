package com.kollu.springbootfilebulk.util;
import java.io.FileWriter;
import java.io.IOException;


public class Testt {

	    public static void main(String[] args) {
	        String fileName = "products_large.csv";
	        try (FileWriter writer = new FileWriter(fileName)) {
	            // Header
	            writer.append("id|name|price|category\n");

	            // 10,000 Records
	            for (int i = 1; i <= 10000; i++) {
	                writer.append(String.valueOf(i))
	                      .append("|kollu").append(String.valueOf(i))
	                      .append("|").append(String.format("%.2f", 10.0 + i * 0.1))
	                      .append("|item").append(String.valueOf((i % 10) + 1))
	                      .append("\n");
	            }
	            System.out.println("Generated 10,000 records in " + fileName);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
