package omnikryptec.util;

import static omnikryptec.util.AdvancedFile.NOT_FOUND;

/**
 *
 * @author Panzer1119
 */
public class Util {
    
    public static final String replaceAll(String string, String toReplace, String with) {
        if(string == null || toReplace == null || with == null) {
            return "";
        }
        while(string.indexOf(toReplace) != NOT_FOUND) {
            string = string.replace("\\" + toReplace, with);
        }
        return string;
    }
   
    
    public static final String[] adjustLength(String[] array, boolean returnnew){
    	String[] names = array;
    	int tmp = 0;
		for(int i=0; i<names.length; i++){
			tmp = Math.max(tmp, names[i].length());
		}
		if(returnnew){
			array = new String[names.length];
		}
		StringBuilder s;
		for(int i=0; i<names.length; i++){
			s = new StringBuilder(names[i]);
			while(s.length()<tmp){
				s.append(' ');
			}
			array[i] = s.toString();
		}
		return array;
    }
    
    public static final String[] merge(Object... b){
    	String[][] array=createString2d(b); 
    	System.out.println(array[0].length);
    	String[] newone = new String[]{""};
    	for(int i=0; i<array.length; i++){
    		newone = mergeS(newone, array[i]);
    	}
    	return newone;
    }
    
    public static final String[] mergeS(String[] a, String[] b){
    	if(a.length==0){
    		return b;
    	}
    	if(b.length==0){
    		return a;
    	}
    	String[] newone = new String[Math.max(a.length, b.length)];
    	for(int i=0; i<newone.length; i++){
    		newone[i] = (a.length==1?a[0]:a[i]) + (b.length==1?b[0]:b[i]);
    	}
    	return newone;
    }
    
    private static String[][] createString2d(Object[] objs){
    	String[][] array = new String[objs.length][];
    	for(int i=0; i<array.length; i++){
    		if(objs[i] instanceof String[]){
    			array[i] = (String[])objs[i];
    		}else if(objs[i] instanceof String){
    			array[i] = new String[]{(String)objs[i]};
    		}
    	}
    	return array;
    }
    
    
}
