package compenland.pictureviewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class ImageDialog extends Dialog{

	public ImageView mDialog;
	public ImageSaver file;
	public Dialog dialog;
	Bitmap myMap;
	Activity b;
	GrabImage grabber;
	FrameLayout frame;
	public ImageDialog(Activity a, ImageSaver file) {
		super(a);
		b = a;
		this.file = file;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		mDialog = (ImageView)findViewById(R.id.dialogImage);
		grabber = new GrabImage();
		try {
			myMap = grabber.execute(file.getUrl()).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mDialog.setImageBitmap(myMap);
		//mDialog.setLayoutParams(new LayoutParams(Integer.parseInt(file.getWidth()), Integer.parseInt(file.getHeight())));
		mDialog.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		} );
	}
	
	 
	private class GrabImage extends AsyncTask<String,Void,Bitmap>{
		 Bitmap tempMap = null;
			@Override
			protected Bitmap doInBackground(String... params) {
				URL url;
				try {
					url = new URL(params[0]);
				
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        connection.setRequestMethod("GET");
		        connection.setDoInput(true);
		        connection.connect();
		        InputStream input = connection.getInputStream();
		        tempMap = BitmapFactory.decodeStream(input);
				input.close();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return tempMap;
			}
	 }
	 
}
