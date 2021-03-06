package com.herault.comptecible;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.herault.comptecible.utils.Stockage;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.List;


public class Activity_image_resultat extends AppCompatActivity {
    protected Activity context;

 private Stockage stock = null;
 private String round ="";
 private String name = "";
 private Spinner choix_resultat = null;
 private ArrayAdapter adapter_choix = null;
 private ImageView imageView = null;
 private LinearLayout chartContainer = null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_resultat);
        stock = new Stockage();             // init de la classe interface de stockage
        stock.onCreate(this);

        round=this.getIntent().getStringExtra("round");
        name=this.getIntent().getStringExtra("name");

        TextView t_round= findViewById(R.id.air_round);
        t_round.setText(round);
        TextView t_name= findViewById(R.id.air_archer);
        t_name.setText(name);
        imageView = findViewById(R.id.air_image);
        chartContainer = findViewById(R.id.air_layoutImage);

        // Select image resultat
         choix_resultat = findViewById(R.id.air_choix_resultat_);


        adapter_choix = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item
        );
        adapter_choix.add(getResources().getString(R.string.impact));
        adapter_choix.add(getResources().getString(R.string.repartitionPoint));
        adapter_choix.add(getResources().getString(R.string.air_select_rounds));


        /* On definit une présentation du spinner quand il est déroulé         (android.R.layout.simple_spinner_dropdown_item) */
        adapter_choix.setDropDownViewResource(R.layout.spinner_generale);
        //Enfin on passe l'adapter au Spinner et c'est tout
        choix_resultat.setAdapter(adapter_choix);

        choix_resultat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            switch (position) {
                case 0 :
                    chartContainer.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    drawResultRound(round,name);
                    break;
                case 1 :
                    imageView.setVisibility(View.GONE);
                    chartContainer.setVisibility(View.VISIBLE);
                    drawResultImpact(round,name);
                    break ;
                case 2 :
                    imageView.setVisibility(View.GONE);
                    chartContainer.setVisibility(View.VISIBLE);
                    drawResultArcherRound(name);
                    break ;
                }
            }
        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
            // your code here
        }
    });



    }

    private void drawResultRound(String round, String archer)
        {

        //    ImageView imageView = (ImageView) findViewById(R.id.air_layoutImage);
            imageView.setBackground(getResources().getDrawable(R.drawable.ic_cible));
            //   ImageView fantomCible=findViewById(R.id.imageCible);
            Bitmap bitmap;
            //   double xmax = fantomCible.getWidth() ;
            double xmax = 1000;
            //   double ymax = fantomCible.getHeight() ;
            double ymax = 1000;
            double Xscale, Yscale;

            // On récupère la coordonnée sur l'abscisse (X) de l'évènement getWidth()
            Xscale = 10 / xmax;


            // On récupère la coordonnée sur l'ordonnée (Y) de l'évènement getHeight()
            Yscale = 10 / ymax;
            bitmap = Bitmap.createBitmap((int) xmax, (int) ymax, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.translate((int) xmax / 2, (int) ymax / 2);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Resultat_archer resultat_archer;

            double moyX = 0, moyY = 0, nb_valeur_moyenne = 0.;
            long boucle = stock.getarrowIndex(archer, round);
            for (int i = 0; i < boucle; i++) {

                resultat_archer = stock.getResultatArrow(archer, round , i+1);
                if (resultat_archer.x < 100) {
                    nb_valeur_moyenne += 1;
                    moyX += resultat_archer.x;
                    moyY += resultat_archer.y;
                    paint.setColor(Color.BLACK);
                    canvas.drawCircle((float) (resultat_archer.x / Xscale), (float) (resultat_archer.y / Yscale), (float) (0.3 / Xscale), paint);
//                Log.d("CompteCible","trace"+Long.toString(resultat_archer.arrow)+" "+Double.toString(resultat_archer.x)+" "+Double.toString(resultat_archer.y));
                }
            }
            paint.setColor(Color.GREEN);
            canvas.drawCircle((float) (moyX / nb_valeur_moyenne / Xscale), (float) (moyY / nb_valeur_moyenne / Yscale), (float) (0.2 / Xscale), paint);
            imageView.setImageBitmap(bitmap);

        }

     //graphe Impact by arrow

     private void drawResultImpact(String round, String archer)
     {
          String[] xLabel = new String[]{
                 "0", "1", "2", "3", "4", "5",
                 "6", "7", "8", "9", "10"
         };
        int[] x = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };


        // Creating an XYSeries for Height
        XYSeries expenseSeries = new XYSeries(getResources().getString(R.string.air_TitleGraphe_arrow_by_arrow));
        // Adding data to Height Series
         Long archer_id =stock.getArcherId(archer);
         Double maxValue= 0.;
        for (int i = 0; i < x.length; i++) {
            String tempo = stock.getResultatRoundCompte(round,Long.toString(archer_id), Integer.toString(i));
            Double valueTemp = Double.valueOf(tempo) ;
            expenseSeries.add(i,valueTemp);

            if (valueTemp> maxValue)
                    maxValue= valueTemp ;
        }
        // Creating a dataset to hold height series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        // Adding Height Series to dataset
        dataset.addSeries(expenseSeries);

        // Creating XYSeriesRenderer to customize expenseSeries
        XYSeriesRenderer heightRenderer = new XYSeriesRenderer();
        heightRenderer.setColor(Color.GREEN);
        heightRenderer.setFillPoints(true);
        heightRenderer.setDisplayChartValues(true);
        heightRenderer.setChartValuesTextSize(40);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setXLabels(0);
        renderer.setChartTitle(getResources().getString(R.string.air_TitleGraphe_arrow_by_arrow));
        renderer.setXTitle(getResources().getString(R.string.air_Tittle_Axe_X));
        renderer.setYTitle(getResources().getString(R.string.air_Tittle_Axe_Y));

        /***
         * Customizing graphs
         */
        // setting text size of the title
        renderer.setChartTitleTextSize(30);

        renderer.setLabelsColor(Color.BLACK);
        renderer.setGridColor(Color.GRAY);
        // setting text size of the axis title
        renderer.setAxisTitleTextSize(50);
        // setting text size of the graph lable
        renderer.setLabelsTextSize(30);
        // setting zoom buttons visiblity
        renderer.setZoomButtonsVisible(true);
        // setting pan enablity which uses graph to move on both axis
        renderer.setPanEnabled(true, true);
        // setting click false on graph
        renderer.setClickEnabled(false);
        // setting zoom to false on both axis
        renderer.setZoomEnabled(false, false);
        // setting lines to display on y axis
        renderer.setShowGridY(true);
        // setting lines to display on x axis
        renderer.setShowGridX(true);
        // setting legend to fit the screen size
        renderer.setFitLegend(true);
        // setting displaying line on grid
        renderer.setShowGrid(true);
        // setting zoom to false
        renderer.setZoomEnabled(true);
        // setting external zoom functions to false
        renderer.setExternalZoomEnabled(false);
        // setting displaying lines on graph to be formatted(like using
        // graphics)
        renderer.setAntialiasing(true);
        // setting to in scroll to false
        renderer.setInScroll(false);
        // setting to set legend height of the graph
        renderer.setLegendHeight(50);
         renderer.setLegendTextSize(40);
        // setting x axis label align
        renderer.setXLabelsAlign(Paint.Align.CENTER);
        renderer.setXLabelsColor(Color.WHITE);
        renderer.setXLabelsAngle(0);
        // setting y axis label to align
        renderer.setYLabelsAlign(Paint.Align.LEFT);
        // setting text style
        renderer.setTextTypeface("sans_serif", Typeface.BOLD);
        // setting number of values to display in y axis
        renderer.setYLabels(40);
        renderer.setYAxisColor(Color.BLACK);
        //setting x axis min value
        renderer.setYAxisMin(0);
        // setting y axis max value
        renderer.setYAxisMax(maxValue+(maxValue/10));
        // setting used to move the graph on xaxiz to .5 to the right
        renderer.setXAxisMin(-0.5);
        // setting used to move the graph on xaxiz to .5 to the right
        renderer.setXAxisMax(x.length);
        // setting bar size or space between two bars
        renderer.setBarSpacing(1);
        // Setting background color of the graph to transparent
        renderer.setBackgroundColor(Color.DKGRAY);
        // Setting margin color of the graph to transparent
        renderer.setMarginsColor(getResources().getColor(android.R.color.transparent));
        renderer.setApplyBackgroundColor(true);
        renderer.setScale(1f);
        // setting x axis point size
        renderer.setPointSize(1f);
        // setting the margin size for the graph in the order top, left, bottom,
        // right
        renderer.setMargins(new int[] {60,60,60,60});


         renderer.setXLabelsPadding(10);
        for (int i = 0; i < x.length; i++) {
            renderer.addXTextLabel(i, xLabel[i]);


            //renderer.setYLabelsPadding(10);
        }

        // Adding heightRender to multipleRenderer
        // Note: The order of adding dataseries to dataset and renderers to
        // multipleRenderer
        // should be same
        renderer.addSeriesRenderer(heightRenderer);

        // this part is used to display graph on the xml

        // remove any views before u paint the chart
        chartContainer.removeAllViews();
        //drawing bar chart

         GraphicalView chart = ChartFactory.getBarChartView(Activity_image_resultat.this, dataset, renderer, Type.STACKED);
        // adding the view to the linearlayout
        chartContainer.addView(chart);

    }

    private void drawResultArcherRound(String archer)
    {
        List<Resultat_archer> lresultat;
        String[] friends = new String[]{
                "0", "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "10"
        };



        // Creating an XYSeries for Height
        XYSeries expenseSeries = new XYSeries(getResources().getString(R.string.air_Tittle_Axe_X_round));
        // Adding data to Height Series
        lresultat = stock.getResultatAllRound(archer);
        int i = 0;
        long value_max = 0;
        for (Resultat_archer r : lresultat) {
             expenseSeries.add(i,r.value);
             if (r.value > value_max)
                 value_max = r.value ;
            i++;
        }


        // Creating a dataset to hold height series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        // Adding Height Series to dataset
        dataset.addSeries(expenseSeries);

        // Creating XYSeriesRenderer to customize expenseSeries
        XYSeriesRenderer heightRenderer = new XYSeriesRenderer();
        heightRenderer.setColor(Color.GREEN);
        heightRenderer.setFillPoints(true);
        heightRenderer.setDisplayChartValues(true);
        heightRenderer.setChartValuesTextSize(40);
        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setXLabels(0);
        i= 0;
        for (Resultat_archer r : lresultat) {
            renderer.addXTextLabel(i, r.getName());
            renderer.setXLabelsPadding(10);
            i++;
        }

        renderer.setChartTitle(getResources().getString(R.string.air_TitleGraphe_rounsd));
        renderer.setXTitle(getResources().getString(R.string.air_Tittle_Axe_X_round));
        renderer.setYTitle(getResources().getString(R.string.air_Tittle_Axe_Y_Score));

        /***
         * Customizing graphs
         */
        // setting text size of the title
        renderer.setChartTitleTextSize(40);
        renderer.setLabelsColor(Color.WHITE);
        renderer.setGridColor(Color.YELLOW);
        // setting text size of the axis title
        renderer.setAxisTitleTextSize(40);
        // setting text size of the graph lable
        renderer.setLabelsTextSize(40);
        // setting zoom buttons visiblity
        renderer.setZoomButtonsVisible(true);
        // setting pan enablity which uses graph to move on both axis
        renderer.setPanEnabled(true, true);
        // setting click false on graph
        renderer.setClickEnabled(false);
        // setting zoom to false on both axis
        renderer.setZoomEnabled(false, false);
        // setting lines to display on y axis
        renderer.setShowGridY(true);
        // setting lines to display on x axis
        renderer.setShowGridX(true);
        // setting legend to fit the screen size
        renderer.setFitLegend(true);
        // setting displaying line on grid
        renderer.setShowGrid(true);
        // setting zoom to false
        renderer.setZoomEnabled(true);
        // setting external zoom functions to false
        renderer.setExternalZoomEnabled(false);
        // setting displaying lines on graph to be formatted(like using
        // graphics)
        renderer.setAntialiasing(true);
        // setting to in scroll to false
        renderer.setInScroll(false);
        // setting to set legend height of the graph
        renderer.setLegendHeight(40);
        renderer.setLegendTextSize(40);
        // setting x axis label align
        renderer.setXLabelsAlign(Paint.Align.CENTER);
        renderer.setXLabelsColor(Color.WHITE);
        renderer.setXLabelsAngle(0);
        // setting y axis label to align
        renderer.setYLabelsAlign(Paint.Align.LEFT);
        // setting text style
        renderer.setTextTypeface("sans_serif", Typeface.BOLD);
        // setting number of values to display in y axis
        renderer.setYLabels(40);
        //setting x axis min value
        renderer.setYAxisMin(0);
        // setting y axis max value
        renderer.setYAxisMax(value_max+(value_max/10));
        // setting used to move the graph on xaxiz to .5 to the right
        renderer.setXAxisMin(-0.5);
        // setting used to move the graph on xaxiz to .5 to the right
        renderer.setXAxisMax(lresultat.size());
        // setting bar size or space between two bars
        renderer.setBarSpacing(0.5);
        // Setting background color of the graph to transparent
        renderer.setBackgroundColor(Color.DKGRAY);
        // Setting margin color of the graph to transparent
        renderer.setMarginsColor(getResources().getColor(android.R.color.transparent));
        renderer.setApplyBackgroundColor(true);
        renderer.setScale(1f);
        // setting x axis point size
        renderer.setPointSize(6f);
        // setting the margin size for the graph in the order top, left, bottom,
        // right
        renderer.setMargins(new int[] { 60,60,60,60});

        // Adding heightRender to multipleRenderer
        // Note: The order of adding dataseries to dataset and renderers to
        // multipleRenderer
        // should be same
        renderer.addSeriesRenderer(heightRenderer);

        // this part is used to display graph on the xml

        // remove any views before u paint the chart
        chartContainer.removeAllViews();
        //drawing bar chart

        GraphicalView chart = ChartFactory.getBarChartView(Activity_image_resultat.this, dataset, renderer, Type.DEFAULT);
        // adding the view to the linearlayout
        chartContainer.addView(chart);

    }

    /*********************************************************************************/
    /** Managing LifeCycle and database open/close operations ************************/
    /*********************************************************************************/
    @Override
    protected void onResume() {
        super.onResume();
        // Open stockage
        stock.openDB();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Close stockage
        stock.closeDB();
    }

}
