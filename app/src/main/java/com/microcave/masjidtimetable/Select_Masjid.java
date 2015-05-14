package com.microcave.masjidtimetable;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Select_Masjid extends ActionBarActivity {

    ListView MasjidList;
    ArrayList<String> al;
    ArrayList<String> SearchedDataResult;
    ArrayAdapter<String> adapter;
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__masjid);

        al=new ArrayList<String>();
        MasjidList=(ListView) findViewById(R.id.masjid_list);
        new HttpAsyncTask().execute("http://www.masjid-timetable.com/data/masjids.php");

        final EditText searchbox= (EditText)findViewById(R.id.search);
        SearchedDataResult= new ArrayList<String>();
        final String value=searchbox.getText().toString();


         final TextWatcher    myhandler = new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {



            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            if(count!=0)

                //Toast.makeText(Select_Masjid.this,"IN FUNCTION",Toast.LENGTH_SHORT).show();

                for(int i=0; i<al.size();i++)
                {
                    String[] str;
                    if(al.get(i).contains(value))
                    {
                        Log.e("custom error", al.get(i).toString());

                        SearchedDataResult.add(al.get(i));


                    }
                }
                adapter = new ArrayAdapter<String>(Select_Masjid.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1,SearchedDataResult);//new String[]{al.get(i)}
                MasjidList.setAdapter(adapter);


                count++;
            }

            public void afterTextChanged(Editable s)
            {

            }
        };

        searchbox.addTextChangedListener(myhandler);


    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls)
        {
            return GetDataFromWebservice.GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result)
        {
            try {
                JSONArray arr = new JSONArray(result);
                JSONObject obj;
                for(int i=0;i<arr.length();i++){
                    obj=arr.getJSONObject(i);
                    al.add(obj.getString("masjid_name")+"\n"+obj.getString("masjid_local_area")
                            +"\n"+obj.getString("masjid_larger_area")+","+obj.getString("masjid_country"));
                }
                 adapter = new ArrayAdapter<String>(Select_Masjid.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, al);


                // Assign adapter to ListView
                MasjidList.setAdapter(adapter);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

}

