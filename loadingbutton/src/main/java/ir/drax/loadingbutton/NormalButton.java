package ir.drax.loadingbutton;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.ImageViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NormalButton extends ConstraintLayout implements View.OnLongClickListener, View.OnClickListener {
    private ContentLoadingProgressBar progressBar;
    private TextView titleTV;
    private ImageView iconTV;
    private float ALPHA_DIS = 0.5f;
    private boolean circularLoading = true;
    private boolean isEnabled = true;
    private String title = "";
    private Drawable icon ;
    private Method mHandler;
    private int textColor=0, iconTint = 0,progressColor=0;
    private LongClickListener longClickListener;
    private ClickListener clickListener;


    public NormalButton(Context context) {
        super(context);
        init();
    }

    public NormalButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        syncAttrs(context,attrs);
        init();
    }

    public NormalButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        syncAttrs(context,attrs);
        init();
    }

    private void syncAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.NormalButton, 0, 0);
        isEnabled = a.getBoolean(R.styleable.NormalButton_enabled,true);

        circularLoading = a.getBoolean(R.styleable.NormalButton_circular_loading,true);

        title = a.getString(R.styleable.NormalButton_text);
        String onclick = a.getString(R.styleable.NormalButton_onClick);

        if (onclick != null) {

            try {
                mHandler = getContext().getClass().getMethod(onclick,
                        View.class);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("Could not find a method " +
                        onclick + "(View) in the activity", e);
            }
        }


        Drawable drawable = a.getDrawable(R.styleable.NormalButton_src);
        if (drawable != null)
            icon = drawable;

        textColor=a.getColor(R.styleable.NormalButton_text_color,getResources().getColor(R.color.colorAccent));
        iconTint =a.getColor(R.styleable.NormalButton_icon_tint,getResources().getColor(R.color.grey));
        progressColor=a.getColor(R.styleable.NormalButton_loading_color,getResources().getColor(R.color.colorAccent));

        a.recycle();

    }

    public NormalButton init(){
        removeAllViews();
        setOnClickListener(this);
        setOnLongClickListener(this);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.normal_button_main, this, true);
        setBackground();


        setCircularLoading(circularLoading);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, progressColor));

        titleTV = findViewById(R.id.title);
        iconTV = findViewById(R.id.icon);
        setTitle(title);
        setIcon(icon);
        setIconTint(iconTint);


        if (isEnabled) ready(true);//Default state ..
        else busy(true);

        return this;
    }

    private void setBackground() {
        if (getBackground()==null)
            setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }

    public void ready(){
        ready(false);}
    public void ready(boolean forced){
        if (isEnabled && !forced)return;
        else isEnabled=true;

        progressBar.hide();
        titleTV.setAlpha(1f);
        iconTV.setAlpha(1f);
    }
    public void busy(){
        busy(false);}
    public void busy(boolean forced){
        if (isEnabled || forced)isEnabled=false;
        else return;

        progressBar.show();
        titleTV.setAlpha(ALPHA_DIS);
        iconTV.setAlpha(ALPHA_DIS);
    }

    @Override
    public boolean onLongClick(View v) {
        if (longClickListener !=null && isEnabled) {
            busy();
            longClickListener.longClicked(this);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null && isEnabled) {
            busy();
            clickListener.clicked(this);

        }else if (mHandler != null && isEnabled) {
            try {
                busy();
                mHandler.invoke(getContext(), this);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Could not execute non "
                        + "public method of the activity", e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("Could not execute "
                        + "method of the activity", e);
            }
        }
    }

    public NormalButton setLongClickListener(LongClickListener longClickListener) {
        this.longClickListener = longClickListener;
        return this;
    }

    public NormalButton setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    public NormalButton setCircularLoading(boolean circularLoading) {
        this.circularLoading = circularLoading;
        if (circularLoading)
            progressBar = findViewById(R.id.loading_button_circular_loading);
        else
            progressBar = findViewById(R.id.loading_button_horizontal_loading);

        return this;
    }

    public NormalButton setTitle(String title) {
        this.title = title;
        titleTV.setText(title);
        titleTV.setTextColor(textColor);

        return this;
    }

    public NormalButton setIcon(Drawable icon) {
        this.icon = icon;
        if (icon == null)iconTV.setVisibility(GONE);
        else
            iconTV.setImageDrawable(icon);

        return this;
    }

    public void disable(){
        setAlpha(0.5f);
        isEnabled =false;
    }

    public void enable(){
        setAlpha(1.0f);
        isEnabled =true;
    }

    public int getIconTint() {
        return iconTint;
    }

    public void setIconTint(int iconTint) {
        this.iconTint = iconTint;
        ImageViewCompat.setImageTintList(iconTV, ColorStateList.valueOf(iconTint));
    }
}

