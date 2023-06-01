package com.example.osmdroidex2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.milestones.MilestoneBitmapDisplayer;
import org.osmdroid.views.overlay.milestones.MilestoneDisplayer;
import org.osmdroid.views.overlay.milestones.MilestoneLineDisplayer;
import org.osmdroid.views.overlay.milestones.MilestoneLister;
import org.osmdroid.views.overlay.milestones.MilestoneManager;
import org.osmdroid.views.overlay.milestones.MilestoneMeterDistanceLister;
import org.osmdroid.views.overlay.milestones.MilestoneMeterDistanceSliceLister;
import org.osmdroid.views.overlay.milestones.MilestonePathDisplayer;
import org.osmdroid.views.overlay.milestones.MilestoneVertexLister;

import java.util.ArrayList;
import java.util.List;

public class Animation {
    private MapView map;
    private ArrayList<GeoPoint> waypoints;
    //polyline animata
    public static final String TITLE = "10K race in Paris";
    private static final float LINE_WIDTH_BIG = 6;
    private static final float TEXT_SIZE = 20;
    private double mAnimatedMetersSoFar;
    private boolean mAnimationEnded;
    private static final int COLOR_POLYLINE_STATIC = Color.BLUE;
    private static final int COLOR_POLYLINE_ANIMATED = Color.GREEN;
    private static final int COLOR_BACKGROUND = Color.WHITE;
    Context ctx;
    Polyline line;


    public Animation(MapView map, ArrayList<GeoPoint> waypoints,Context ctx){
        this.map=map;
        this.waypoints=waypoints;
        this.ctx=ctx;
    }

    public void addOverlays() {
        map.getOverlayManager().remove(line);
        line = new Polyline(map);
        line.getOutlinePaint().setColor(COLOR_POLYLINE_STATIC);
        line.getOutlinePaint().setStrokeWidth(LINE_WIDTH_BIG);
        line.setPoints(waypoints);
        line.getOutlinePaint().setStrokeCap(Paint.Cap.ROUND);
        final List<MilestoneManager> managers = new ArrayList<>();
        //è come se questi valori per ora si settano come se fossero degli oggetti vuoti, per poi
        //prendere la linea quando andiamo a settare line.setMilestoneManagers(managers);
        final MilestoneMeterDistanceSliceLister slicerForPath = new MilestoneMeterDistanceSliceLister();
        final Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(), org.osmdroid.library.R.drawable.next);
        final MilestoneMeterDistanceSliceLister slicerForIcon = new MilestoneMeterDistanceSliceLister();
        managers.add(getAnimatedPathManager(slicerForPath));
        managers.add(getAnimatedIconManager(slicerForIcon, bitmap));
        managers.add(getHalfKilometerManager());
        managers.add(getKilometerManager());
        managers.add(getStartManager(bitmap));
        line.setMilestoneManagers(managers);
        map.getOverlayManager().add(line);
        final ValueAnimator percentageCompletion = ValueAnimator.ofFloat(0, 1000); // 10 kilometers
        percentageCompletion.setDuration(15000); // 5 seconds
        percentageCompletion.setStartDelay(1000); // 1 second
        percentageCompletion.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatedMetersSoFar = (float) animation.getAnimatedValue();
                slicerForPath.setMeterDistanceSlice(0, mAnimatedMetersSoFar);
                slicerForIcon.setMeterDistanceSlice(mAnimatedMetersSoFar, mAnimatedMetersSoFar);
                map.invalidate();
            }
        });
        percentageCompletion.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationEnded = true;
            }
        });
        percentageCompletion.start();
    }

    private MilestoneManager getAnimatedPathManager(final MilestoneLister pMilestoneLister) {
        //come la linea dovrà colorarsi e che proprietà dovrà avere
        final Paint slicePaint = getStrokePaint(COLOR_POLYLINE_ANIMATED, LINE_WIDTH_BIG);
        //listener per capire a che punto è la linea assegnandogli come dovrà colorarla tramitite il
        //MilestoneLineDisplayer
        return new MilestoneManager(pMilestoneLister, new MilestoneLineDisplayer(slicePaint));
    }

    private Paint getStrokePaint(final int pColor, final float pWidth) {
        final Paint paint = new Paint();
        paint.setStrokeWidth(pWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(pColor);
        paint.setStrokeCap(Paint.Cap.ROUND);
        return paint;
    }

    private MilestoneManager getAnimatedIconManager(final MilestoneLister pMilestoneLister,
                                                    final Bitmap pBitmap) {
        //il valore 0 dice che è orientata sulla linea e quindi se metto true fa tutto lui
        //pBitMap serve per indicare l'icona che dovrà seguire il percorso.
        //Stessa cosa di prima ma per l'icona.
        return new MilestoneManager(
                pMilestoneLister,
                new MilestoneBitmapDisplayer(0, true, pBitmap,
                        pBitmap.getWidth() / 2, pBitmap.getHeight() / 2)
        );
    }

    private MilestoneManager getHalfKilometerManager() {
        final Path arrowPath = new Path(); // a simple arrow towards the right
        //è un disegno di una freccia
        arrowPath.moveTo(-5, -5);
        //crea una linea tra i 2 punti
        arrowPath.lineTo(5, 0);
        arrowPath.lineTo(-5, 5);
        arrowPath.close();
        final Paint backgroundPaint = getFillPaint(COLOR_BACKGROUND);

        //Displayer of `MilestoneStep`s as `Path` MilestonePathDisplayer, dove MilestoneStep sono dei punti
        //cardine con un orientamento.

        //MilestoneMeterDistanceLister:Elencare i vertici per una sezione del percorso tra due distanze
        return new MilestoneManager( // display an arrow at 500m every 1km
                new MilestoneMeterDistanceLister(500),
                new MilestonePathDisplayer(0, true, arrowPath, backgroundPaint) {
                    @Override
                    protected void draw(final Canvas pCanvas, final Object pParameter) {
                        //forse continua a colorare la linea finquando non è arrivato a un vertice
                        final int halfKilometers = (int) Math.round(((double) pParameter / 500));
                        if (halfKilometers % 2 == 0) {
                            return;
                        }
                        super.draw(pCanvas, pParameter);
                    }
                }
        );
    }

    private Paint getFillPaint(final int pColor) {
        final Paint paint = new Paint();
        paint.setColor(pColor);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        return paint;
    }

    private MilestoneManager getKilometerManager() {
        final float backgroundRadius = 20;
        final Paint backgroundPaint1 = getFillPaint(COLOR_BACKGROUND);
        final Paint backgroundPaint2 = getFillPaint(COLOR_POLYLINE_ANIMATED);
        final Paint textPaint1 = getTextPaint(COLOR_POLYLINE_STATIC);
        final Paint textPaint2 = getTextPaint(COLOR_BACKGROUND);
        final Paint borderPaint = getStrokePaint(COLOR_BACKGROUND, 2);
        return new MilestoneManager(
                //Elencare i vertici per una sezione del percorso tra due distanze
                new MilestoneMeterDistanceLister(1000),
                //con false cambio orientation in base al punto in cui sono, con true continuo sulla stessa lina
                new MilestoneDisplayer(0, false) {
                    @Override
                    protected void draw(final Canvas pCanvas, final Object pParameter) {
                        final double meters = (double) pParameter;
                        final int kilometers = (int) Math.round(meters / 1000);
                        final boolean checked = meters < mAnimatedMetersSoFar || (kilometers == 10 && mAnimationEnded);
                        final Paint textPaint = checked ? textPaint2 : textPaint1;
                        final Paint backgroundPaint = checked ? backgroundPaint2 : backgroundPaint1;
                        final String text = "" + kilometers + "K";
                        final Rect rect = new Rect();
                        textPaint1.getTextBounds(text, 0, text.length(), rect);
                        pCanvas.drawCircle(0, 0, backgroundRadius, backgroundPaint);
                        pCanvas.drawText(text, -rect.left - rect.width() / 2, rect.height() / 2 - rect.bottom, textPaint);
                        pCanvas.drawCircle(0, 0, backgroundRadius + 1, borderPaint);
                    }
                }
        );
    }

    private Paint getTextPaint(final int pColor) {
        final Paint paint = new Paint();
        paint.setColor(pColor);
        paint.setTextSize(TEXT_SIZE);
        paint.setAntiAlias(true);
        return paint;
    }

    private MilestoneManager getStartManager(final Bitmap pBitmap) {
        return new MilestoneManager(
                //Listing every vertex
                new MilestoneVertexLister(),
                new MilestoneBitmapDisplayer(0, true,
                        pBitmap, pBitmap.getWidth() / 2, pBitmap.getHeight() / 2) {
                    @Override
                    protected void draw(final Canvas pCanvas, final Object pParameter) {
                        if (0 != (int) pParameter) { // we only draw the start
                            return;
                        }
                        super.draw(pCanvas, pParameter);
                    }
                }
        );
    }

}
