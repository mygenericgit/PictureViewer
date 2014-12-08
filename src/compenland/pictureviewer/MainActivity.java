package compenland.pictureviewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import compenland.pictureviewer.Constants;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class MainActivity extends Activity{
	String authURL, tokenURL, request_token;
	JSONObject json;
	String getPictures;
	JSONcall caller;
	JSONArray pictures;
	LinearLayout linear;
	ImageView[] image;
	ArrayList<ArrayList<ImageSaver>> threepic;
	public Bitmap myBitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_viewer);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		linear = (LinearLayout)findViewById(R.id.pictureHolder);
		getPictures = Constants.APIURL+"/tags/selfie/media/recent?client_id="+Constants.CLIENTID;
		threepic = new ArrayList<ArrayList<ImageSaver>>();
		caller = new JSONcall();
		caller.execute();
		
	}
	
	public void displayImages(){
		boolean bigPicture = true;
		int smallCount = 0;
		ArrayList<ImageSaver> imageArray;
		ImageSaver fullImage;
		GrabImage grabber;
		try {
			
			pictures = json.getJSONArray(Constants.TAG_DATA);
			
			
			for(int i = 0; i < pictures.length(); i++){
				imageArray= new ArrayList<ImageSaver>();
				JSONObject c = pictures.getJSONObject(i);
				fullImage = new ImageSaver();
				JSONObject image = c.getJSONObject(Constants.TAG_IMAGES);
				
				
					JSONObject lowres = image.getJSONObject(Constants.TAG_LOWRESOLUTION);
					fullImage.setUrl(lowres.getString("url"));
					fullImage.setWidth(lowres.getString("width"));
					fullImage.setHeight(lowres.getString("height"));
					imageArray.add(fullImage);

					fullImage = new ImageSaver();
					JSONObject thumbnail = image.getJSONObject(Constants.TAG_THUMBNAIL);
					fullImage.setUrl(thumbnail.getString("url"));
					fullImage.setWidth(thumbnail.getString("width"));
					fullImage.setHeight(thumbnail.getString("height"));
					imageArray.add(fullImage);
					
					fullImage = new ImageSaver();
					JSONObject standardres = image.getJSONObject(Constants.TAG_THUMBNAIL);
					fullImage.setUrl(standardres.getString("url"));
					fullImage.setWidth(standardres.getString("width"));
					fullImage.setHeight(standardres.getString("height"));
					imageArray.add(fullImage);
					
				threepic.add(imageArray);
					
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		 image = new ImageView[threepic.size()];
		for(int i =0; i < 10;i++){
			 
			 grabber = new GrabImage();
			 imageArray = threepic.get(i);
			 if(smallCount == 2){
				 bigPicture = true;
				 smallCount = 0;
			 }
			 if(bigPicture){
				 bigPicture = false;
				 fullImage = imageArray.get(0);
				 
			 }else{
				 fullImage = imageArray.get(1);
				 smallCount++;
			 }
			 try {
				myBitmap = grabber.execute(fullImage.getUrl()).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 image[i] = new ImageView(this);
			 image[i].setImageBitmap(myBitmap);
			 image[i].setId(i);
			 image[i].setTag(imageArray.get(2));
			 image[i].setLayoutParams(new LayoutParams(Integer.parseInt(fullImage.getWidth()),Integer.parseInt(fullImage.getHeight())));
			 linear.addView(image[i]);
			 image[i].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ImageDialog dialog = new ImageDialog(MainActivity.this,(ImageSaver) image[v.getId()].getTag());
					dialog.show();
				}
			});
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	 private class JSONcall extends AsyncTask<Void,Void,Void>{

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				JSONParser parser = new JSONParser();
				json = parser.getJSONFromUrl(getPictures);
				return null;
			}
	    	
	    	public void onPostExecute(Void cha){
	    		displayImages();
	    	}
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
