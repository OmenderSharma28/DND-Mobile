package com.donotdisturb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.constant.Constant;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListFile extends ListActivity {
	private String[] absolutePath;
	private String[] fileType=new String[]{"csv"};
	private String directory="/sdcard/";

	private List<String> ContactFiles = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		ListView lv= getListView();
		
		ContactFiles = FindFiles();
		setListAdapter(new ArrayAdapter<String>(ListFile.this, android.R.layout.simple_list_item_1,
				ContactFiles));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> aView, View v,
					int position, long id) {
				//Toast.makeText(getApplicationContext(), ""+absolute[position],Toast.LENGTH_SHORT).show();
				Intent intent= new Intent();
				intent.putExtra(Constant.KEY_FILE,absolutePath[position]);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	/**
	 * 
	 *  Finding files of given extension
	 */
	private List<String> FindFiles() {
		final List<String> tFileList = new ArrayList<String>();
		FilenameFilter[] filter = new FilenameFilter[fileType.length];
		int i = 0;
		for (final String type : fileType) {
			filter[i] = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith("." + type);
				}
			};
			i++;
		}
		FileUtils fileUtils = new FileUtils();
		File[] allMatchingFiles = fileUtils.listFilesAsArray(
				new File(directory), filter, -1);
		absolutePath=new String[allMatchingFiles.length];
		int j=0;
		for (File f : allMatchingFiles) {
			absolutePath[j++]=f.getAbsolutePath();
			tFileList.add(f.getName());
//			Log.e("",""+f.getAbsolutePath());
		}
		return tFileList;
	}
	public class FileUtils {
		public void saveArray(String filename, List<String> output_field) {
			try {
				FileOutputStream fos = new FileOutputStream(filename);
				GZIPOutputStream gzos = new GZIPOutputStream(fos);
				ObjectOutputStream out = new ObjectOutputStream(gzos);
				out.writeObject(output_field);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.getStackTrace();
			}
		}

		@SuppressWarnings("unchecked")
		public List<String> loadArray(String filename) {
			try {
				FileInputStream fis = new FileInputStream(filename);
				GZIPInputStream gzis = new GZIPInputStream(fis);
				ObjectInputStream in = new ObjectInputStream(gzis);
				List<String> read_field = (List<String>) in.readObject();
				in.close();
				return read_field;
			} catch (Exception e) {
				e.getStackTrace();
			}
			return null;
		}

		public File[] listFilesAsArray(File directory, FilenameFilter[] filter,
				int recurse) {
			Collection<File> files = listFiles(directory, filter, recurse);
			File[] arr = new File[files.size()];
			return files.toArray(arr);
		}
		public Collection<File> listFiles(File directory,
				FilenameFilter[] filter, int recurse) {
			Vector<File> files = new Vector<File>();
			File[] entries = directory.listFiles();
			if (entries != null) {
				for (File entry : entries) {
					for (FilenameFilter filefilter : filter) {
						if (filter == null
								|| filefilter
								.accept(directory, entry.getName())) {
							files.add(entry);
							Log.v("FileUtils", "Added: "
									+ entry.getName());
						}
					}
					if ((recurse <= -1) || (recurse > 0 && entry.isDirectory())) {
						recurse--;
						files.addAll(listFiles(entry, filter, recurse));
						recurse++;
					}
				}
			}
			return files;
		}
	}
}
