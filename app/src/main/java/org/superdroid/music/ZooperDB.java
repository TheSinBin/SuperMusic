package org.superdroid.music;

/*
 ----------------------------
  This DB project is started
     by Furkan Karcıoğlu
        25.08.2017 Fri
 ----------------------------
  Last Edit: 29.08.2017 Tue
 ----------------------------
*/

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import org.apache.commons.codec.binary.*;

public class ZooperDB {

	/*
	 Components:
	 1- Start
	 2- End
	 3- Split1 character
	 4- Split2 character
	 5- Split3 character
	 6- Split4 character
	 7- Split5 character
	 8- Split6 character (BETA)
	 9- Put Any Object character
	 */

	private String [] components = {
		"<",">",";","•",",","_","—","~","\\"
	};

	private String fileName;
	private String fileFormat = "dtr";

	ZooperDB(String dbName, String packageName){
		String fn = "/data/data/"+packageName+"/zooper_prefs";
		File folder = new File(fn);
		if(!folder.exists()) folder.mkdir();
		fileName = fn+"/"+dbName+"."+fileFormat;
		read();
	}

	public int getLength(){
		return sb1.toString().split(components[4]).length;
	}
	
	public String[][] getStringArrayArray(String key, String[]... def){
		String s = hm1.get(key);
		String[][] x = {};
		String[] prex = {};
		if(s != null && s != ""){
			String[] pre = s.split(components[6]);
			int i = 0;
			x = new String[pre.length][pre.length];
			for(String ss : pre){
				prex = ss.split(components[5]);
				int a = 0;
				for(String su : prex){
					prex[a] = decode(su);
					a++;
				}
				x[i] = prex;
				i++;
			}
			return x;
		} return def;
	}
	
	public int[][] getIntegerArrayArray(String key, int[]... def){
		String s = hm1.get(key);
		int[][] x = {};
		String[] prex = {};
		int[] y = {};
		if(s != null && s != ""){
			String[] pre = s.split(components[6]);
			int i = 0;
			x = new int[pre.length][pre.length];
			for(String ss : pre){
				prex = ss.split(components[5]);
				int a = 0;
				y = new int[prex.length];
				for(String su : prex){
					y[a] = Integer.parseInt(decode(su));
					a++;
				}
				x[i] = y;
				i++;
			}
			return x;
		} return def;
	}
	
	public float[][] getFloatArrayArray(String key, float[]... def){
		String s = hm1.get(key);
		float[][] x = {};
		String[] prex = {};
		float[] y = {};
		if(s != null && s != ""){
			String[] pre = s.split(components[6]);
			int i = 0;
			x = new float[pre.length][pre.length];
			for(String ss : pre){
				prex = ss.split(components[5]);
				int a = 0;
				y = new float[prex.length];
				for(String su : prex){
					y[a] = Float.parseFloat(decode(su));
					a++;
				}
				x[i] = y;
				i++;
			}
			return x;
		} return def;
	}
	
	public double[][] getDoubleArrayArray(String key, double[]... def){
		String s = hm1.get(key);
		double[][] x = {};
		String[] prex = {};
		double[] y = {};
		if(s != null && s != ""){
			String[] pre = s.split(components[6]);
			int i = 0;
			x = new double[pre.length][pre.length];
			for(String ss : pre){
				prex = ss.split(components[5]);
				int a = 0;
				y = new double[prex.length];
				for(String su : prex){
					y[a] = Double.parseDouble(decode(su));
					a++;
				}
				x[i] = y;
				i++;
			}
			return x;
		} return def;
	}
	
	public boolean[][] getBooleanArrayArray(String key, boolean[]... def){
		String s = hm1.get(key);
		boolean[][] x = {};
		String[] prex = {};
		boolean[] y = {};
		if(s != null && s != ""){
			String[] pre = s.split(components[6]);
			int i = 0;
			x = new boolean[pre.length][pre.length];
			for(String ss : pre){
				prex = ss.split(components[5]);
				int a = 0;
				y = new boolean[prex.length];
				for(String su : prex){
					y[a] = Boolean.parseBoolean(decode(su));
					a++;
				}
				x[i] = y;
				i++;
			}
			return x;
		} return def;
	}
	
	public void putStringArrayArray(String key, String[]... value){
		StringBuilder sba = new StringBuilder();
		StringBuilder sbb = new StringBuilder();
		for(int i = 0;i != value.length;i++){
			for(int n = 0;n != value[i].length;n++)
				sba.append(encode(value[i][n])+components[5]);
			sbb.append(sba.replace(sba.length()-1,sba.length(),"").toString()+components[6]);
			sba = new StringBuilder();
		}
		sbb.replace(sbb.length()-2,sbb.length(),"");
		hm1.put(key,sbb.toString());
		if(!(sb1.toString().contains(key+components[4])))
			sb1.append(key+components[4]);
	}
	
	public void putIntegerArrayArray(String key, int[]... value){
		StringBuilder sba = new StringBuilder();
		StringBuilder sbb = new StringBuilder();
		for(int i = 0;i != value.length;i++){
			for(int n = 0;n != value[i].length;n++)
				sba.append(encode(value[i][n]+"")+components[5]);
			sbb.append(sba.replace(sba.length()-1,sba.length(),"").toString()+components[6]);
			sba = new StringBuilder();
		}
		sbb.replace(sbb.length()-2,sbb.length(),"");
		hm1.put(key,sbb.toString());
		if(!(sb1.toString().contains(key+components[4])))
			sb1.append(key+components[4]);
	}
	
	public void putFloatArrayArray(String key, float[]... value){
		StringBuilder sba = new StringBuilder();
		StringBuilder sbb = new StringBuilder();
		for(int i = 0;i != value.length;i++){
			for(int n = 0;n != value[i].length;n++)
				sba.append(encode(value[i][n]+"")+components[5]);
			sbb.append(sba.replace(sba.length()-1,sba.length(),"").toString()+components[6]);
			sba = new StringBuilder();
		}
		sbb.replace(sbb.length()-2,sbb.length(),"");
		hm1.put(key,sbb.toString());
		if(!(sb1.toString().contains(key+components[4])))
			sb1.append(key+components[4]);
	}
	
	public void putDoubleArrayArray(String key, double[]... value){
		StringBuilder sba = new StringBuilder();
		StringBuilder sbb = new StringBuilder();
		for(int i = 0;i != value.length;i++){
			for(int n = 0;n != value[i].length;n++)
				sba.append(encode(value[i][n]+"")+components[5]);
			sbb.append(sba.replace(sba.length()-1,sba.length(),"").toString()+components[6]);
			sba = new StringBuilder();
		}
		sbb.replace(sbb.length()-2,sbb.length(),"");
		hm1.put(key,sbb.toString());
		if(!(sb1.toString().contains(key+components[4])))
			sb1.append(key+components[4]);
	}
	
	public void putBooleanArrayArray(String key, boolean[]... value){
		StringBuilder sba = new StringBuilder();
		StringBuilder sbb = new StringBuilder();
		for(int i = 0;i != value.length;i++){
			for(int n = 0;n != value[i].length;n++)
				sba.append(encode(value[i][n]+"")+components[5]);
			sbb.append(sba.replace(sba.length()-1,sba.length(),"").toString()+components[6]);
			sba = new StringBuilder();
		}
		sbb.replace(sbb.length()-2,sbb.length(),"");
		hm1.put(key,sbb.toString());
		if(!(sb1.toString().contains(key+components[4])))
			sb1.append(key+components[4]);
	}
	
	public String[] getStringArray(String key, String... def){
		String s = hm1.get(key);
		if(s !=null && s!= ""){
			String[] pre = s.split(components[5]);
			StringBuilder sb = new StringBuilder();
			for(String p : pre)
				sb.append(decode(p)+components[5]);
			return sb.toString().split(components[5]);
		}

		return def;
	}

	public int[] getIntegerArray(String key, int... def){
		String s = hm1.get(key);
		int i = 0;
		int[] num = new int[def.length];
		if(s != null && s != ""){
			String[] pre = s.split(components[5]);
			StringBuilder sb = new StringBuilder();
			for(String p : pre)
				sb.append(decode(p)+components[5]);
			String[] sa = sb.toString().split(components[5]);
			for(String x : sa){
				num[i] = Integer.parseInt(x);
				i++;
			}
			return num;
		} return def;
	}

	public float[] getFloatArray(String key, float... def){
		String s = hm1.get(key);
		int i = 0;
		float[] num = new float[def.length];
		if(s != null && s != ""){
			String[] pre = s.split(components[5]);
			StringBuilder sb = new StringBuilder();
			for(String p : pre)
				sb.append(decode(p)+components[5]);
			String[] sa = sb.toString().split(components[5]);
			for(String x : sa){
				num[i] = Float.parseFloat(x);
				i++;
			}
			return num;
		} return def;
	}

	public double[] getDoubleArray(String key, double... def){
		String s = hm1.get(key);
		int i = 0;
		double[] num = new double[def.length];
		if(s != null && s != ""){
			String[] pre = s.split(components[5]);
			StringBuilder sb = new StringBuilder();
			for(String p : pre)
				sb.append(decode(p)+components[5]);
			String[] sa = sb.toString().split(components[5]);
			for(String x : sa){
				num[i] = Double.parseDouble(x);
				i++;
			}
			return num;
		} return def;
	}

	public boolean[] getBooleanArray(String key, boolean... def){
		String s = hm1.get(key);
		int i = 0;
		boolean[] num = new boolean[def.length];
		if(s != null && s != ""){
			String[] pre = s.split(components[5]);
			StringBuilder sb = new StringBuilder();
			for(String p : pre)
				sb.append(decode(p)+components[5]);
			String[] sa = sb.toString().split(components[5]);
			for(String x : sa){
				num[i] = Boolean.parseBoolean(x);
				i++;
			}
			return num;
		} return def;
	}

	public void putStringArray(String key, String... value){
		StringBuilder sb = new StringBuilder();
		for(String x : value)
			sb.append(encode(x)+components[5]);
		sb.replace(sb.length()-1,sb.length(),"");
		hm1.put(key,sb.toString());
		if(!(sb1.toString().contains(key+components[4])))
			sb1.append(key+components[4]);
	}

	public void putIntegerArray(String key, int... value){
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i != value.length;i++)
			sb.append(encode(value[i]+"")+components[5]);
		sb.replace(sb.length()-1,sb.length(),"");
		hm1.put(key,sb.toString());
		if(!(sb1.toString().contains(key+components[4])))
			sb1.append(key+components[4]);
	}

	public void putFloatArray(String key, float... value){
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i != value.length;i++)
			sb.append(encode(value[i]+"")+components[5]);
		sb.replace(sb.length()-1,sb.length(),"");
		hm1.put(key,sb.toString());
		if(!(sb1.toString().contains(key+components[4])))
			sb1.append(key+components[4]);
	}

	public void putDoubleArray(String key, double... value){
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i != value.length;i++)
			sb.append(encode(value[i]+"")+components[5]);
		sb.replace(sb.length()-1,sb.length(),"");
		hm1.put(key,sb.toString());
		if(!(sb1.toString().contains(key+components[4])))
			sb1.append(key+components[4]);
	}

	public void putBooleanArray(String key, boolean... value){
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i != value.length;i++)
			sb.append(encode(value[i]+"")+components[5]);
		sb.replace(sb.length()-1,sb.length(),"");
		hm1.put(key,sb.toString());
		if(!(sb1.toString().contains(key+components[4])))
			sb1.append(key+components[4]);
	}
	
	public String getString(String key, String def){
		String s = hm1.get(key);
		if(s != null && s != "")
			return decode(s);
		return def;
	}

	public int getInteger(String key, int def){
		String s = hm1.get(key);
		if(s != null && s != "")
			return Integer.parseInt(decode(s));
		return def;
	}

	public float getFloat(String key, float def){
		String s = hm1.get(key);
		if(s != null && s != "")
			return Float.parseFloat(decode(s));
		return def;
	}

	public double getDouble(String key, double def){
		String s = hm1.get(key);
		if(s != null && s != "")
			return Double.parseDouble(decode(s));
		return def;
	}

	public boolean getBoolean(String key, boolean def){
		String s = hm1.get(key);
		if(s != null && s != "")
			return Boolean.parseBoolean(decode(s));
		return def;
	}

	public void putString(String key, String value){
		value = encode(value);
		hm1.put(key,value);
		if(!(sb1.toString().contains(key+components[4])))
			sb1.append(key+components[4]);
	}

	public void putInteger(String key, int value){
		String val = encode(value+"");
		hm1.put(key,val);
		if(!(sb1.toString().contains(key+components[4])))
			sb1.append(key+components[4]);
	}

	public void putFloat(String key, float value){
		String val = encode(value+"");
		hm1.put(key,val);
		if(!(sb1.toString().contains(key+components[4])))
			sb1.append(key+components[4]);
	}

	public void putDouble(String key, double value){
		String val = encode(value+"");
		hm1.put(key,val);
		if(!(sb1.toString().contains(key+components[4])))
			sb1.append(key+components[4]);
	}

	public void putBoolean(String key, boolean value){
		String val = encode(value+"");
		hm1.put(key,val);
		if(!(sb1.toString().contains(key+components[4])))
			sb1.append(key+components[4]);
	}
	
	public void removeValue(String key){
		hm1.remove(key);
		String t = sb1.toString().replace(key+components[4],"");
		sb1 = new StringBuilder();
		sb1.append(t);
	}

	public void removeDB(){
		hm1.clear();
		sb1 = new StringBuilder();
		new File(fileName).delete();
	}

	public void refresh(){
		write();
		read();
	}

	private void write(){
		StringBuilder wr = new StringBuilder();
		String[] key = sb1.toString().split(components[4]);
		for(int i = 0;i != key.length;i++)
			wr.append(components[0]+key[i]+components[3]+hm1.get(key[i])+components[1]+components[2]);
		wr.replace(wr.length()-1,wr.length(),"");
		if(!(wr.toString().equals(components[0]+components[3]+"null"+components[1]))){
			try{
				File f = new File(fileName);
				OutputStream os = new FileOutputStream(f);
				for(byte x : wr.toString().getBytes())
					os.write(x);
				os.close();
			} catch(Exception e){}
		}

	}

	private void read(){
		StringBuilder rd = new StringBuilder();
		sb1 = new StringBuilder();
		try{
			File f = new File(fileName);
			Scanner sc = new Scanner(f);
			while(sc.hasNext())
				rd.append(sc.next());
			String[] pv = rd.toString().replaceAll(components[0],"")
				.replaceAll(components[1],"").split(components[2]);
			for(int i = 0;i != pv.length;i++){
				String[] xx = pv[i].split(components[3]);
				hm1.put(xx[0],xx[1]);
				sb1.append(xx[0]+components[4]);
			}
		} catch(Exception e){}
	}

	private String encode(String s){
		String enc = new String(Base64.encodeBase64(s.getBytes()),StandardCharsets.UTF_8);
		String[] en = enc.split("");
		StringBuilder sb1 = new StringBuilder();
		for(int i = 0;i != en.length;i++)
			sb1.append(en[i]+randomChar());
		return sb1.toString();
	}

	private String decode(String s){
		for(int i = 0;i != enc_dec_str.length;i++)
			s = s.replaceAll(enc_dec_str[i],"");
		return new String(Base64.decodeBase64(s.getBytes()),StandardCharsets.UTF_8);
	}

	private String[] enc_dec_str = {
		"∆", "¶", "℅", "÷", "°", "©",
		"®", "™", "π", "¥", "£", "$",
		"¢", "×", "√", "/", "`", "€",
		"Π", "|", "§", "♪"
	};

	int old = 0;

	int randomNumber(){
		int nnum = new Random().nextInt(enc_dec_str.length);
		if(nnum == old)
			return randomNumber();
		old = nnum;
		return nnum;
	}

	private String randomChar(){
		String t = "";
		for(int i = 0;i < new Random().nextInt(4)+1;i++)
			t += enc_dec_str[randomNumber()];
		return t;
	}

	private StringBuilder sb1 = new StringBuilder();
	private HashMap<String,String> hm1 = new HashMap<String,String>();

}
