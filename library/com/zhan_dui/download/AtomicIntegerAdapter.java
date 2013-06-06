package com.zhan_dui.download;

import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class AtomicIntegerAdapter extends XmlAdapter<Integer, AtomicInteger> {

	@Override
	public Integer marshal(AtomicInteger v) throws Exception {
		return v.get();
	}

	@Override
	public AtomicInteger unmarshal(Integer v) throws Exception {
		return new AtomicInteger(v);
	}

}
