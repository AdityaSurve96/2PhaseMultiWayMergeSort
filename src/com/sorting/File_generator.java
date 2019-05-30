package com.sorting;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Scanner;
public class File_generator 
{
	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		System.out.println("Enter No of Tuples");
		Scanner sc = new Scanner(System.in);
		int noOfTuples=sc.nextInt();
		sc.nextLine();
		System.out.println("Enter the main memory limit if desired as 1mb or 20mb ,etc in general Xmb \n OR enter ANY if not desired:");

		String ram=sc.nextLine();
		sc.close();
		String inputFile="InputFile_"+noOfTuples+"Tuples_"+ram+"MainMemoryLimit.txt";
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(inputFile)))) {
			writer.write(Integer.toString(noOfTuples)+ " "+ram);
			writer.newLine();
			writer.newLine();
			Random rand = new Random();
			System.out.println("File Generating...");

			for(int i=0;i<noOfTuples;i++)
			{
				int  n = rand.nextInt(99999999) ;
				writer.write(Integer.toString(n));
				writer.write("\t"); 
			}
			System.out.println("Input File Generated as "+inputFile);

		}
	}
}
