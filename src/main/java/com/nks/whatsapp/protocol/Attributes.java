package com.nks.whatsapp.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Attributes<K extends Comparable<K>> {

private Map<K,Object> innerMap;
private List<OrderedEntry<K>> keys;
private int modCount;
private boolean keysSorted=false;

public 	Attributes(){
this(null,null);
}

public 	Attributes(K[] keys,Object[] values){
innerMap=new HashMap<K,Object>();
this.keys=new ArrayList<OrderedEntry<K>>();
if(keys==null)return;
for(int i=0;i<keys.length;i++)
	put(keys[i],values[i]);
}

public List<K> keys(){
if(!keysSorted)
	{Collections.sort(keys);keysSorted=true;}
List<K> ret=new ArrayList<K>();
for(OrderedEntry<K> key:this.keys)
	ret.add(key.key);
return ret;
}

public Object get(K key){
return innerMap.get(key);
}

public Object put(K key,Object value){
Object oldVal=innerMap.put(key,value);
if(oldVal!=null)
	this.keys.remove(new OrderedEntry<K>(key,0));
keys.add(new OrderedEntry<K>(key,modCount++));	
keysSorted=false;
return oldVal;
}

public int size(){
	return innerMap.size();
}

@Override
public String toString() {
    if (keys.size()==0)
        return "[]";

StringBuilder sb = new StringBuilder();
    sb.append('[');
    for (K key:keys()) {
        sb.append(key);
        sb.append("=");
    	sb.append(get(key));
        sb.append(',').append(' ');
    }
return sb.append(']').toString();
    
}

public boolean containsKey(K key){
	return innerMap.containsKey(key);
}


private static class OrderedEntry<K extends Comparable<K>> implements Comparable<OrderedEntry<K>>{
	private int order;
	private K key;

	private OrderedEntry(K key,int order){
		this.order=order;
		this.key=key;
	}

	@Override
	public int compareTo(OrderedEntry<K> o) {
	return order-o.order;
	}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof OrderedEntry)
			return key.equals(((OrderedEntry)obj).key);
	return false;
	}

	
}


}
