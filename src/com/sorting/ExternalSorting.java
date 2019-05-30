package com.sorting;
import java.io.*;
import java.util.*;


public class ExternalSorting {

	int blockSize;
	int tempfilenumber = 0;
	int numberoftempfiles = 0;
	int defaultBufferSize = 8096;

	public long getInputRecordCount(BufferedReader inputFile) throws IOException {
		String firstLine = inputFile.readLine();
		inputFile.readLine();
		return Long.parseLong(firstLine.split(" ")[0]);
	}

	public long getInputRecordCount2(BufferedReader inputFile) throws IOException {
		String firstLine = inputFile.readLine();
		inputFile.readLine();
		inputFile.readLine();
		return Long.parseLong(firstLine.split(" ")[0]);
	}

	public int calculateBlockSize(double multiplier_phase1) {

		long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long freeMemory = Runtime.getRuntime().maxMemory() - usedMemory;
		long usableMemory = (long) (freeMemory * multiplier_phase1);
		return (int) Math.ceil(usableMemory / 5);

	}

	public int calculateBuffers(double multiplier_phase2) {

		long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long freeMemory = Runtime.getRuntime().maxMemory() - usedMemory;
		long usableMemory = (long) (freeMemory * multiplier_phase2);
		System.out.println("Usable Memory : " + usableMemory);
		return (int) Math.ceil(usableMemory / defaultBufferSize);
	}


	public String convertFile(String inputFile) throws IOException 
	{

		FileInputStream fis = new FileInputStream(inputFile);
		BufferedInputStream bis = new BufferedInputStream(fis);

		String newfile="IgnoreThisFile_Temporary_File_InputConvertedtoNewLineDelimited.txt";
		FileOutputStream fos = new FileOutputStream(newfile);

		BufferedOutputStream bos = new BufferedOutputStream(fos);

		int n = -1;


		while ((n = bis.read())!= - 1)
		{
			if((char)n == '\t' ||(char)n ==' ')
			{

				bos.write((char)'\r');
				bos.write((char)'\n');
			}
			else bos.write((char)n);
		}
		bos.flush();
		bis.close();
		bos.close();

		return newfile;
	}

	public void writeChunks(int[] lstArr) throws IOException {
		BufferedWriter bw = null;
		try {
			// this part names the new temperoray files automatically as test0,
			// test1 ,test2,....
			String fileName = "Phase1_File_" + tempfilenumber + ".txt";
			tempfilenumber++;
			bw = new BufferedWriter(new FileWriter(fileName));


			for (int a : lstArr) {
				bw.write("" + a);
				bw.newLine();

			}

			numberoftempfiles++;

		} catch (Exception ex) {
			System.out.println("Something went wrong in writeChunks Phase 1 : " + ex.getMessage());
		} finally {
			bw.flush();
			bw.close();
		}
	}



	public boolean phase1(String filename, double multiplier_phase1) throws IOException {

		BufferedReader br = null;

		try {

			// open the buffered reader for input

			int counter = 0;

			int[] lstArr = null;

			br = new BufferedReader(new FileReader(filename));

			String record = null;



			long noOfRecords = getInputRecordCount2(br);

			long revCounter = noOfRecords;

			blockSize = calculateBlockSize(multiplier_phase1);

			while ((record = br.readLine()) != null) {

				if (lstArr == null) {

					blockSize = (revCounter - (long) blockSize >= 0) ? blockSize : (int) revCounter;

					lstArr = new int[blockSize];

				}



				lstArr[counter] = Integer.parseInt(record.trim());

				counter++;

				if (counter == blockSize) {



					Arrays.sort(lstArr);


					writeChunks(lstArr);

					counter = 0;

					lstArr = null;

					revCounter = revCounter - (long) blockSize;

					System.gc();


				}

			}
			br.close();
		}
		catch(OutOfMemoryError o)
		{
			return false;
		}
		catch (Exception ex) 
		{
			return false;
		} 
		return true;
	}

	public boolean phase2(double multiplier_phase2,String inputFileName) throws IOException 
	{
		int noOfBuffers = calculateBuffers(multiplier_phase2);

		boolean isNotCompleted = true;
		int passFiles = numberoftempfiles;
		int counter = 0;
		BufferedReader[] inputBuffers = null;
		ArrayList<Integer> mergeArray;
		int min = -1;
		int minIndex = -1;
		int pass = 0;
		int tempFileNo = 0;
		String phase = "Phase1_File_";
		String tempfilename = phase + counter + ".txt";
		int nextInt = 0;

		ArrayList<String> Files = null;

		// initialize pass
		try {
			while (isNotCompleted) {
				// open required number of buffers and also check if opening max
				// number is required
				int maxBuffers = (passFiles < noOfBuffers) ? passFiles : noOfBuffers;

				System.out.println("Max Buffers : " + maxBuffers);
				passFiles = passFiles - maxBuffers;

				inputBuffers = new BufferedReader[maxBuffers];
				mergeArray = new ArrayList<Integer>();
				if(Files==null)
				{
					Files= new ArrayList<String>();
				}
				// file numbering starts from 0
				// initial merge array with smallest integers
				for (int i = 0; i < maxBuffers; i++) {
					// file opening logic
					tempfilename = phase + (phase == "Phase1_File_" ? "" : (pass - 1) + "File") + counter + ".txt";
					counter++;

					inputBuffers[i] = new BufferedReader(new FileReader(tempfilename),2048);
					mergeArray.add(Integer.parseInt(inputBuffers[i].readLine()));
					Files.add(tempfilename);
				}

				String tempfile = "Phase2_Pass_" + pass + "File" + tempFileNo + ".txt";

				BufferedWriter bw = new BufferedWriter(new FileWriter(tempfile));
				while (maxBuffers > 0) {

					// replace with binary search
					// check smallest number
					for (int x = 0; x < mergeArray.size(); x++) 
					{

						if (min == -1 && mergeArray.get(x) != null) 
						{
							min = mergeArray.get(x);
							minIndex = x;
						} else if (mergeArray.get(x) != null && mergeArray.get(x) <= min) {
							min = mergeArray.get(x);
							minIndex = x;
						}

					}
					String te = inputBuffers[minIndex].readLine();
					if (te != null) 
					{
						nextInt = Integer.parseInt(te);
						mergeArray.set(minIndex, nextInt);
					} else 
					{
						inputBuffers[minIndex].close();
						maxBuffers--;
						mergeArray.set(minIndex, null);

					}

					// write to list
					bw.write("" + min);
					bw.newLine();
					min = -1;
					minIndex = -1;
				}
				bw.flush();
				bw.close();
				

				// refresh values
				mergeArray = null;
				inputBuffers = null;
				tempFileNo++;

				// pass completion logic
				if (passFiles == 0) 
				{
					// exit condition
					if (tempFileNo == 1) {
						isNotCompleted = false;
						System.out.println("Writing to output file..."+" OUTPUT_OF_"+inputFileName);
						BufferedWriter bw2 = new BufferedWriter(new FileWriter("OUTPUT_OF_"+inputFileName+".txt"));
						BufferedReader br2 = new BufferedReader(new FileReader(tempfile));
						String s;

						while ((s = br2.readLine()) != null) { // read a line
							bw2.write(s);
							bw2.newLine();// write to output file
							bw2.flush();
						}
						br2.close();
						bw2.close();
						File toDelete = new File (tempfile);
						toDelete.delete();

					}
					passFiles = tempFileNo;
					if (phase == "Phase1_File_") 
					{
						phase = "Phase2_Pass_";
					}
					pass++;

					tempFileNo = 0;

					counter = 0;

					for(String str: Files)
					{
						File f = new File (str);
						f.delete();

					}
					Files=null;
				}

				System.gc();
			}
			
		} 
		catch(OutOfMemoryError o)
		{
			return false;
		}
		catch (Exception ex) 
		{
			System.out.println("Phase 2 execution failed:");
			return false;

		}
		return true;
	}



	public static void main(String[] args) throws IOException 
	{
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter input file name as FileName.txt");

		String input = sc.nextLine();
		sc.close();
		long starttime= System.nanoTime();
		boolean result_phase1 = false;
		boolean result_phase2 = false;
		ExternalSorting obj = new ExternalSorting();
		double multiplier_phase1 = 0.70;
		double multiplier_phase2 = 0.70;
		String filename = obj.convertFile(input);
		
		do {

			result_phase1 = obj.phase1(filename,multiplier_phase1);
			if (!result_phase1 && multiplier_phase1>0.0 ) 
			{
				multiplier_phase1 -= 0.10;				

			}

			if(result_phase1==true)
			{
				result_phase2 = obj.phase2(multiplier_phase2,input);


				if (!result_phase2 && multiplier_phase2 >0.0) {
					multiplier_phase2-=0.10;

				}

			}

		} while (!result_phase2 && !result_phase1);
		long endtime= System.nanoTime();
		System.out.println("Exexution time is ::"+((endtime-starttime)/1000000000)+"Seconds");

		System.out.println("Output File generated");
	}
}