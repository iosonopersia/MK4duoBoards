package utilities;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import model.Const;

public class SecureTokenizer {
	
	public static String readToken(StringTokenizer st){
		try{
			return st.nextToken().trim();
		}catch(NoSuchElementException e){
			return Const.EMPTY;
		}
	}
	
	public static String readToken(StringTokenizer st, String delim){
		try{
			return st.nextToken(delim).trim();
		}catch(NoSuchElementException e){
			return Const.EMPTY;
		}
	}
	
	public static String readInlineCommentToken(StringTokenizer st){
		try{
			return st.nextToken(System.lineSeparator()).trim();
		}catch(NoSuchElementException e){
			return Const.EMPTY;
		}
	}

	public static String readFileName(StringTokenizer st) {
		StringBuilder sb=new StringBuilder();
		while(st.hasMoreTokens()){
			sb.append(st.nextToken());
			sb.append(Const.SPACE);
		}
		return sb.toString().trim();
	}
}
