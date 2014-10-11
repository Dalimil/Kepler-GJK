package gjk.kepler;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class Home extends Activity {

    private String url_suplovani = "http://old.gjk.cz/suplovani.php";
    private TextView text_suplovani;
    private HTML_Loader myHTML;

    private String[] myNavigationNames;
    private DrawerLayout myDrawerLayout;
    private ListView myDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        myNavigationNames = getResources().getStringArray(R.array.navigation);
        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        myDrawerList = (ListView) findViewById(R.id.navigation_drawer);
        // Nastavit adapter pro List View - ten naplní navigation_drawer položkami TextView,
        // které jsou specifikované v drawer_list_item.xml
        myDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, myNavigationNames));
        // Zareagovat na kliknutí
        myDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        text_suplovani = (TextView) findViewById(R.id.text_suplovani);
        myHTML = new HTML_Loader(this);
        this.getPage(url_suplovani); //načti suplování při prvním spuštění
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    private void selectItem(int position) {
        myDrawerList.setItemChecked(position, true);
        setTitle(myNavigationNames[position]);
        myDrawerLayout.closeDrawer(myDrawerList);
    }


    private void getPage(String myURL){
        if(myHTML.checkConnection()){
            Toast.makeText(this, "Aktualizuji...", Toast.LENGTH_LONG).show();
            new DownloadWebpageTask().execute(myURL);
        }else{
            //není připojení
            Toast.makeText(this, "Nejste připojeni k internetu.", Toast.LENGTH_LONG).show();
        }
    }
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        //stáhne webovou stránku v novém vlákně - jinak by se UI sekalo
        @Override
        protected String doInBackground(String... urls) {
            return myHTML.getHTML(urls);
        }
        @Override
        protected void onPostExecute(String result) {
            if(result == null) {
                Toast.makeText(Home.this, "Nelze získat webovou stránku. Chybná URL?", Toast.LENGTH_LONG).show();
            }else{
                show(result);
            }
        }
    }

    private void show(String s){
        text_suplovani.setText(s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //vytvoří položky v horní liště (action bar)
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Akce po kliknutí na jednotlivé položky v horní liště (action bar)
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                this.getPage(url_suplovani);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
