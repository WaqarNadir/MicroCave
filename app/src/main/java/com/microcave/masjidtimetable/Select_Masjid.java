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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;


public class Select_Masjid extends ActionBarActivity {

    ListView MasjidList;
    ArrayList<String> al;
    ArrayList<String> Masjid;
    ArrayList<String> Local_Area;
    ArrayList<String> Larger_area;
    ArrayList<String> SearchedDataResult;
    ArrayAdapter<String> adapter;
    String temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__masjid);

        al=new ArrayList<String>();
        Masjid=new ArrayList<String>();
        Local_Area=new ArrayList<String>();
        Larger_area=new ArrayList<String>();
        SearchedDataResult= new ArrayList<String>();
        MasjidList=(ListView) findViewById(R.id.masjid_list);
        final EditText searchbox= (EditText)findViewById(R.id.search);
        // -------------------------get data from web service --------------------
        new HttpAsyncTask().execute("http://www.masjid-timetable.com/data/masjids.php");
        //-------------------------------------------------------------------------
// ----------------Search function calling on text changed-----------------------

         final TextWatcher    myhandler = new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                SearchedDataResult.clear();         // deletes the previous data of searched
               String value=searchbox.getText().toString();

                for(int i=0; i<Masjid.size();i++)
                {
                    temp=Masjid.get(i).toString();              // to check ignorecase sensitivity
                    if( temp.toLowerCase().contains(value.toLowerCase())  )
                    {
                        //first select the desired Masjid
                        for(int val =0; val<al.size();val++ )
                        {           //Now check that specific mosque in all list
                            if(al.get(val).contains(Masjid.get(i)))         // check masjid in al list
                            {
                                SearchedDataResult.add(al.get(val) );
                            }
                        }

                    }
                }
                String[] _localarea;
                for (int j=0; j< Local_Area.size();j++)
                {
                           _localarea= Local_Area.get(j).split(" ");        // spliting on space for searching complete Name
                    for(int inner=0; inner< _localarea.length;inner++)
                    {
                       //search specific area
                        temp= _localarea[inner].toLowerCase();                  // ignore Case Senstivity
                        if(temp.startsWith(value.toLowerCase()))
                        {
                            for(int val =0; val<al.size();val++ )
                            {       //Now check that area in All lsit
                                if(al.get(val).contains(Local_Area.get(j)))         // check Local area in al list
                                {
                                    SearchedDataResult.add(al.get(val) );
                                }
                            }

                        }
                    }

                }

                for(int i=0; i<Larger_area.size();i++)
                {
                    temp=Larger_area.get(i).toString();              // to check ignorecase sensitivity
                    if( temp.toLowerCase().startsWith(value.toLowerCase())  )
                    {       //Larger area identified
                        for(int val =0; val<al.size();val++ )
                        {
                            if(al.get(val).contains(Larger_area.get(i)))         // check Larger area in al list
                            {// searched area in al list
                                SearchedDataResult.add(al.get(val) );
                            }
                        }

                    }
                }

                adapter = new ArrayAdapter<String>(Select_Masjid.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1,SearchedDataResult);//new String[]{al.get(i)}
                MasjidList.setAdapter(adapter);

            }

            public void afterTextChanged(Editable s)
            {

            }
        };
//-------------------------------------------------------------------------
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
                    Masjid.add(i,obj.getString("masjid_name"));
                    Local_Area.add(i,obj.getString("masjid_local_area"));
                    Larger_area.add(i,obj.getString("masjid_larger_area"));
                    al.add(i,Masjid.get(i)+"\n"+
                            Local_Area.get(i) +"\n"+
                            Larger_area.get(i) +" "+obj.getString("masjid_country"));
//                    al.add(obj.getString("masjid_name")+"\n"+obj.getString("masjid_local_area")
//                            +"\n"+obj.getString("masjid_larger_area")+","+obj.getString("masjid_country"));
                }
                Collections.sort(Masjid);
                Collections.sort(Local_Area);
                Collections.sort(Larger_area);
                Collections.sort(al);

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

    //-----------------------Function for slide Alphabets----------------------
    public void scroll(View v) {
        String alphabet = (String)v.getTag();
        int index=SearchMasjid(alphabet);

        MasjidList.setSelectionFromTop(index, 0);
    }

    public int SearchMasjid(String s)
    {
                for(int i=0; i<Masjid.size();i++)
                {
                    temp=Masjid.get(i).toString();              // to check ignorecase sensitivity
                    if( temp.toLowerCase().startsWith(s.toLowerCase())  )
                    {
                        return  i;          // send starting index of Alphabet from list
                    }
                }
                    return 0;
    }
//-----------------------------------------------------------------------------
}

