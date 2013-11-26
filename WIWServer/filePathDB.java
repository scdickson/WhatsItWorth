//Theo Reinke
//Whats it worth 


import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class filePathDB
{
  protected DBConnection conn = null;
  private ArrayList<String> results;
  private static String picDir;
  private static ZipInputStream stream;
  private static PrintWriter writer;
 
 public static void main(String[] args)
 {
   //namesToText("E:\\MTG Picture\\");
   textToDB("H:\\nameList.txt");
 }
  
 //Constructor
 public filePathDB(String url, int port, String username, String password)
 {
   conn = new DBConnection(url, port, username, password);
   conn.connect("WIWDB");
   results = new ArrayList<String>();
 }
 
 //Get the file path of a card based off of the name
 public ArrayList<String> getPath(String name)
 {
  String query = "SELECT folderPath FROM pathCache WHERE cardName LIKE " + name + ";";
  results = conn.query(query);
  return results;
 }
 
 //Get the name of a card based off of the path
 public ArrayList<String> getName(String path)
 {
  String query = "SELECT cardName FROM pathCache WHERE folderPath LIKE " + path + ";";
  results = conn.query(query);
  return results;
 }
 
 //Insert a card into the db
 public void insertCard(String name, String path, String lang)
 {
  name = name.replace("'","\\'");
  path = path.replace("\\", "/");
  String query = "INSERT INTO pathCache (cardName, folderPath, lang) VALUES(\'" + name + "\', \'" + path + "\', \'" + lang + "\' );"; 
  System.out.println(query);
  results = conn.query(query);
  return;
 }
 
 //Import the cards from a text file to the DB
 //use namesToText to get the properly formatted text file
 private static void textToDB(String fileName){
   //change to 192.168.1.12 when running on server
   filePathDB tester = new filePathDB("98.226.145.27", 3306, "root", "kpcofgs");
   //Open file
   String line;
   String delim = "@";
   String[] parts = new String[3];
   Scanner in = null;
   File file = new File(fileName);
   try{
     in = new Scanner(file);
     //Parse line by line, each part seperated by @
     while(in.hasNextLine()){
       line = in.nextLine();
       parts = line.split(delim);
       //write to DB
       tester.insertCard(parts[0], parts[1], parts[2]);
     }
   }
   catch( FileNotFoundException e){
    System.out.println("File " + file.getName() + " not found!"); 
   }
   finally{
     if(in != null)
       in.close();
   }
 }
 
 //Write card names, location, and language to 
 //text file, this avoids moving a large directory
 //around when updating
 private static void namesToText(String topDir){
   File f = new File(topDir);
   crawl(f);
 }
 
 //Explore all subfolders recursively
 private static void crawl(File f)
 {
   try{
     writer = new PrintWriter("E:\\nameList.txt", "UTF-8");
     crawl(f, "");
     writer.close();
   } catch(Exception e){
     System.out.println("Error when reading from file");
   }
 }
 
 private static void crawl(File f, String indent) throws Exception
 {
   if(f.isDirectory()){
     File[] subFiles = f.listFiles();
     indent += "    ";
     for(int i = 0; i < subFiles.length; i++){
       crawl(subFiles[i], indent);
     }
   }
   else{
     try{
       //open the zip folder
       String checkName = f.toString();
       if(checkName.contains(".zip")){
         InputStream file = new FileInputStream(f);
         stream = new ZipInputStream(file);
         ZipEntry entry;
         //zip folder contains jpgs of cards
         while((entry = stream.getNextEntry()) != null){
           //scrub file extension from entry.getName()
           String fileName = entry.getName();
           fileName = fileName.substring(0, fileName.lastIndexOf('.'));
           //Get file path without top directory
           String filePath = f.getPath();
           filePath = filePath.substring(picDir.length(), filePath.length());
           //Get language
           String lang = f.getName();
           lang = lang.substring(0, lang.lastIndexOf('.'));
           writer.println(fileName + "@" + filePath + "@" + lang);
           return;
         }
       }
       else{//No zip folder, take directly from file
         return;
       }
     }
     finally
     {
       stream.close();
     }
   }
 }

}
