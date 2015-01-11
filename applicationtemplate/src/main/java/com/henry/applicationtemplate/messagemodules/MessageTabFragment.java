package com.henry.applicationtemplate.messagemodules;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.henry.applicationtemplate.R;


/**
 * @author henry
 * 消息fragment
 */
public class MessageTabFragment extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_tab_message, container, false);
	}
}
