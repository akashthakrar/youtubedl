import java.net.*;
import java.util.*;
import java.io.*;

class progressbar extends Thread{
  double size;
  String f;
  int x_per = 2;
  progressbar(double size,String f){
    this.size = size;
    this.f = f;
  }

  public void run(){
    String wd = System.getProperty("user.dir");
    File file = new File(wd+"/"+f);
    double dsize = 0;
    System.out.println();
    while(dsize <= 100){
      dsize = ((file.length())/size)*100;
		if(dsize <= 10)
			System.out.print("\r[=>          ]");
		else if(dsize <= 20)
			System.out.print("\r[==>         ]");
	    else if(dsize <= 30)
			System.out.print("\r[===>        ]");
		else if(dsize <= 40)
			System.out.print("\r[====>       ]");
		else if(dsize <= 40)
			System.out.print("\r[=====>      ]");
		else if(dsize <= 50)
			System.out.print("\r[======>     ]");
		else if(dsize <= 60)
			System.out.print("\r[=======>    ]");
		else if(dsize <= 70)
			System.out.print("\r[========>   ]");
		else if(dsize <= 80)
			System.out.print("\r[=========>  ]");
		else if(dsize <= 90)
			System.out.print("\r[==========> ]");
		else if(dsize < 100)
			System.out.print("\r[===========>]");
		else{
			System.out.println("\r[============]" + "\t"+(int)dsize+"%");
			break;
		}
		System.out.print("\t"+(int)dsize+"%");
		try{
			Thread.sleep(100);
		}catch(Exception e){}
    }
  }
}

class youtubedl{
  static URL ylink;
  static HashSet<String> set = new HashSet<String>();
  static List<String> audio_set = new ArrayList<String>();
  static List<String> video_set = new ArrayList<String>();
  static List<String> av_set = new ArrayList<String>();

  youtubedl(URL ylink){
    this.ylink = ylink;
  }

  public static void GetDownloadIndex() throws Exception{
    BufferedReader in = new BufferedReader(new InputStreamReader(ylink.openStream()));
		//BufferedWriter out = new BufferedWriter(new FileWriter("output.html"));
		int b;
		String op=null;
		while((b=in.read()) != -1){
		//	out.write(b);
			op=op+(char)b;
		}
		String[] list = op.split("url=");
		for(String i: list){
			if(i.startsWith("https")){
				set.add(i.split(",")[0]);
			}
		}
    String url=null,itag=null,quality_label=null,type=null;
		for(String i: set){
			String temp = i;
      temp = temp.replace("\\", "\\\\");
      for(String sdata : temp.split("\\\\")){
        if(sdata.startsWith("https"))
          url = sdata;
      }
      if(url!=null){
        if(url.startsWith("https")){
          url=url.split(" ")[0];
          url = URLDecoder.decode(url,"UTF-8");
          if(url.contains("mime="))
            type = (url.split("mime=")[1]).split("&")[0];
          if(url.contains("itag"))
            itag = (url.split("itag=")[1]).split("&")[0];
          if(url.contains("&gir=yes")){
            if(type.startsWith("video"))
              video_set.add(url+"\ntype: "+type+"\nitag: "+itag);
            else if(type.startsWith("audio"))
              audio_set.add(url+"\ntype: "+type+"\nitag: "+itag);
          }
          else
            av_set.add(url+"\ntype: "+type+"\nitag: "+itag);
        }
      }
		}
  }

  public static void displayIndex() throws Exception{
    int count = 0;
    System.out.println("audio Link: ");
    for(String s: audio_set){
      count = count + 1;
      //System.out.println(URLDecoder.decode(s,"UTF-8"));
      System.out.println("[1-"+count+"] " + (s.split("\n")[1]).replace("%2F","\\") + " " + getQuality((s.split("\n")[2]).split(": ")[1]));
    }
    count = 0;
    System.out.println("video Link: ");
    for(String s: video_set){
      count = count + 1;
      //System.out.println(URLDecoder.decode(s,"UTF-8"));
      System.out.println("[2-"+count+"] " + (s.split("\n")[1]).replace("%2F","\\") + " " + getQuality((s.split("\n")[2]).split(": ")[1]));
    }
    count = 0;
    System.out.println("audio/video Link: ");
    for(String s: av_set){
      count = count + 1;
      //System.out.println(URLDecoder.decode(s,"UTF-8"));
      System.out.println("[3-"+count+"] " + (s.split("\n")[1]).replace("%2F","\\") + " " + getQuality((s.split("\n")[2]).split(": ")[1]));
    }
  }

  public static void download (String index,String fname) throws Exception{
    int avd = Integer.parseInt(index.split("-")[0]);
    String type;
    URL ylink_download;
    int dindex = Integer.parseInt(index.split("-")[1]);
    if(avd==3){
      ylink_download = new URL((av_set.get(dindex-1)).split("\n")[0]);
      type = (av_set.get(dindex-1)).split("\n")[1];
    }
    else if(avd==2){
      ylink_download = new URL((video_set.get(dindex-1)).split("\n")[0]);
      type = (video_set.get(dindex-1)).split("\n")[1];
    }
    else if(avd==1){
      ylink_download = new URL((audio_set.get(dindex-1)).split("\n")[0]);
      type = (audio_set.get(dindex-1)).split("\n")[1];
    }
    else{
      System.out.println("Enter valid input");
      return;
    }
    InputStream is = ylink_download.openStream();
    URLConnection con = (URLConnection)ylink_download.openConnection();
    double dsize = con.getContentLength()/1048576;
    System.out.println("\nDownloading : "+dsize+" MB");
    if(con!=null){
      String wd = System.getProperty("user.dir");
      String fo = fname+"."+type.split("%2F")[1];
      File f = new File(wd,fo);

      FileOutputStream fos = new FileOutputStream(f);
      byte[] buffer = new byte[1024];
      progressbar t1 = new progressbar(dsize*1048576,fo);
      t1.start();
      int len1 = 0;
      if(is != null) {
          while ((len1 = is.read(buffer)) > 0) {
              fos.write(buffer,0, len1);
          }
      }
      if(fos != null) {
          fos.close();
      }
    }
    System.out.println("Download Complete");
  }
public static String getQuality(String x)
{
    if(x.equals("5"))
        return "normal, 240p, FLV, 320x240";
    else if(x.equals("17"))
        return "normal, 144p, 3GP, 176x144";
    else if(x.equals("18"))
        return "normal, 360p, MP4, 640x360";
    else if(x.equals("22"))
        return "normal, 720p, MP4, 1280x720";
    else if(x.equals("34"))
        return "normal, 360p, FLV, 640x360";
    else if(x.equals("35"))
        return "normal, 480p, FLV, 854x480";
    else if(x.equals("36"))
        return "normal, 240p, 3GP, 320x240";
    else if(x.equals("37"))
        return "normal, 1080p, MP4, 1920x1080";
    else if(x.equals("38"))
        return "normal, 3072p, MP4, 4096x3072";
    else if(x.equals("43"))
        return "normal, 360p, WebM, 640x360";
    else if(x.equals("44"))
    	return "normal, 480p, WebM, 854x480";
    else if(x.equals("45"))
    	return "normal, 720p, WebM, 1280x720";
    else if(x.equals("46"))
    	return "normal, 1080p, WebM, 1920x1080";
    else if(x.equals("82"))
    	return "normal, 360p, MP4, 640x360-3D";
    else if(x.equals("83"))
    	return "normal, 480p, MP4, 640x480-3D";
    else if(x.equals("84"))
    	return "normal, 720p, MP4, 1280x720-3D";
    else if(x.equals("100"))
    	return "normal, 360p, WebM, 640x360-3D";
    else if(x.equals("102"))
    	return "normal, 720p, WebM, 1280x720-3D";


    else if(x.equals("133"))
        return "video only, 240p, m4v, 426x240";
    else if(x.equals("134"))
        return "video only, 360p, m4v, 640x360";
    else if(x.equals("135"))
        return "video only, 480p, m4v, 854x480";
    else if(x.equals("136"))
        return "video only, 720p, m4v, 1280x720";
    else if(x.equals("137"))
        return "video only, 1080p, m4v, 1920x1080";
    else if(x.equals("138"))
        return "video only, 3072p, m4v, 4096x3072";


    else if(x.equals("139"))
        return "audio only, 48k, m4a";
    else if(x.equals("140"))
        return "audio only, 128k, m4a";
    else if(x.equals("141"))
        return "audio only, 256k, m4a";
    else if(x.equals("160"))
        return "video only, 144p, m4v, 256x144";
    else if(x.equals("167"))
        return "video only, 480p, webm, 640x480";
    else if(x.equals("168"))
        return "video only, 480p, webm, 854x480";
    else if(x.equals("169"))
        return "video only, 720p, webm, 1280x720";
    else if(x.equals("170"))
        return "video only, 1080p, webm, 1920x1080";
    else if(x.equals("171"))
        return "audio only,  128k, ogg";
    else if(x.equals("172"))
        return "audio only, 192k, ogg";
    else if(x.equals("242"))
        return "normal, 240p, webm, 360x240";
    else if(x.equals("243"))
        return "normal, 360p, webm, 480x360";
    else if(x.equals("244"))
        return "normal, 480p, webm, 640x480";
    else if(x.equals("245"))
        return "normal, 480p, webm, 640x480";
    else if(x.equals("246"))
        return "normal, 480p, webm, 640x480";
    else if(x.equals("247"))
        return "normal, 480p, webm, 720x480";
    else if(x.equals("248"))
        return "normal, 1080p, webm,  1920x1080";
    else if(x.equals("256"))
        return "audio, 192k, m4a";
    else if(x.equals("258"))
        return "audio, 320k, m4a";
    else if(x.equals("264"))
        return "video only, 1080p, m4v, 1920x1080";
    else
        return x;
}
  public static void main(String args[]) throws Exception{
    Scanner scn = new Scanner(System.in);
    youtubedl obj = new youtubedl(new URL(args[0]));
    obj.GetDownloadIndex();
    obj.displayIndex();
    System.out.print("Enter Index: ");
    String get_index = scn.next();
    System.out.print("Enter Name: ");
    String get_name = scn.next();
    obj.download(get_index,get_name);
    System.out.println();
  }

}
