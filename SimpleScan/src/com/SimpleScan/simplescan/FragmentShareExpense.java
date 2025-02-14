package com.SimpleScan.simplescan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.SimpleScan.simplescan.Camera.BitmapUtils;
import com.SimpleScan.simplescan.Camera.CameraUtils;
import com.SimpleScan.simplescan.Entities.Category;
import com.SimpleScan.simplescan.Entities.Expense;
import com.SimpleScan.simplescan.sqlite.DBManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class FragmentShareExpense extends Fragment implements View.OnClickListener
{
	private static final String EXPENSE_KEY = "expense_key";
	private Expense expense;
	private ImageView receiptImageView;
	private EditText editName;
	private EditText editDate;
	private EditText editAmount;
	private Spinner spinner ;
	private DBManager dbManager;
	//For setDataFromCam
	private static boolean cameFlag = false;
	private static Expense camExpense = new Expense();
	private static Bitmap receiptImg;
	private static String receiptImgPath;
	
	private Animator mCurAnimator;
	private int mShortAnimDur;
	private static boolean hasImg;
	
	public FragmentShareExpense() 
	{
		// Required empty public constructor
	}
	
	/**
	 * Creates an Expense and creates a FragmentShareExpense with it.
	 * 
	 * @param context The Main activity
	 * @return A FragmentEditExpense that can edit the new expense
	 */
	public static FragmentShareExpense createNewExpense(Context context) {
		// Get today's date to set as default
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		Calendar calendar = Calendar.getInstance();
		
		// Create the expense and save it
		Expense newExpense = new Expense();
		newExpense.setAmount(0.);
		newExpense.setDate(sdf.format(calendar.getTime()));
		newExpense.setTitle("expense");
		// ID is set to -1 to show that it hasn't been created yet
		newExpense.setId(-1);
		
		// Create new instance
		return createNewInstance(newExpense);
	}
	
	/**
	 * Creates a new FragmentShareExpense, passing in an Expense object.
	 * 
	 * @param expense The Expense that should be edited
	 * @return A FragmentEditExpense that can edit that Expense
	 */
	public static FragmentShareExpense createNewInstance(Expense expense) {
		FragmentShareExpense fragment = new FragmentShareExpense();
		Bundle bundle = new Bundle();
		bundle.putSerializable(EXPENSE_KEY, expense);
		fragment.setArguments(bundle);
		
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{	
		View v = inflater.inflate(R.layout.fragment_share_expense, container, false);
		expense = (Expense) getArguments().getSerializable(EXPENSE_KEY);
		
		setUpEditExpense(v);
		setUpReceiptImage(v);
		setUpDatePicker(v);
		setUpCategory(v);
		hasImg = false;
		mShortAnimDur = getResources().getInteger(android.R.integer.config_shortAnimTime);
		
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// check if camera data has been passed
		if(cameFlag)
		{
			// grab the UI Component 
			receiptImageView = (ImageView)getActivity().findViewById(R.id.SE_im);			
			editName = (EditText)getActivity().findViewById(R.id.SE_editName);
			editDate  = (EditText)getActivity().findViewById(R.id.SE_editDate);
			editAmount = (EditText)getActivity().findViewById(R.id.SE_editAmount);
			
			// set the values for UI		
			receiptImageView.setImageBitmap(receiptImg);
			editName.setText(camExpense.getTitle());
			editDate.setText(camExpense.getDate());
			editAmount.setText(String.valueOf(camExpense.getAmount()));
			
			//set the flag back to false
			cameFlag = false;
		}
	}
	private void setUpCategory(View v)
	{
		spinner = (Spinner)v.findViewById(R.id.SE_spinner);
		List<String> cateNameList = new ArrayList<String>();
		List<Category> categoriesList = new ArrayList<Category>();
		dbManager = new DBManager(getActivity());
		categoriesList = dbManager.getCategories();
		for(Category c : categoriesList)
		{
			cateNameList.add(c.getTitle());
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_spinner_item, cateNameList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
	
	private void setUpReceiptImage(View v) {
		ImageView ExpenseImg = (ImageView) v.findViewById(R.id.SE_im);
		if(expense.getImagePath() != null) {
			receiptImg = BitmapUtils.createSampledBitmap(expense.getImagePath(), 1);
		    ExpenseImg.setImageBitmap(receiptImg);
		}
		ExpenseImg.setOnClickListener(this);
		ExpenseImg.setOnLongClickListener(new OnLongClickListener() {
			@Override
		    public boolean onLongClick(View theView) {
		        if(hasImg) {
		        	zoomImgFromThumb(receiptImageView);
		        } else {
		        	if(CameraUtils.checkCameraHardware(getActivity())) startActivity(new Intent(getActivity(), CameraActivity.class));
					else Toast.makeText(getActivity(), "Camera is not supported on your device", Toast.LENGTH_LONG).show();
		        }		        
		        return true;
		    }
		});
	}
	
	private void setUpEditExpense(View v) {
		// Set the default values for text fields
		editName = (EditText) v.findViewById(R.id.SE_editName);
		editName.setText(expense.getTitle());
		editDate = (EditText) v.findViewById(R.id.SE_editDate);
		editDate.setText(expense.getDate());
		editAmount = (EditText) v.findViewById(R.id.SE_editAmount);
		editAmount.setText("" + expense.getAmount());
		
		// Set button listeners
	    Button saveButton = (Button) v.findViewById(R.id.SE_btnSave);
	    saveButton.setOnClickListener(this);
	    Button deleteButton = (Button) v.findViewById(R.id.SE_btnDel);
	    deleteButton.setOnClickListener(this); 
	}
	
	private void setUpDatePicker(View v) {
		final Calendar calendar = Calendar.getInstance();
		Button datePickerBtn = (Button) v.findViewById(R.id.SE_btnDatePicker);
		
		final DatePickerDialog.OnDateSetListener datePicker =
				new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				calendar.set(Calendar.YEAR,  year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
				editDate.setText(sdf.format(calendar.getTime()));
			}
		};
		datePickerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new DatePickerDialog(getActivity(), 
						datePicker, 
						calendar.get(Calendar.YEAR), 
						calendar.get(Calendar.MONTH), 
						calendar.get(Calendar.DAY_OF_MONTH)
						).show();
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case (R.id.SE_btnSave):
			saveExpense();
			break;
		case (R.id.SE_btnDel):
			deleteExpense();
			break;
		case (R.id.SE_im):
			// To Tai: u can just change the class name here to navigate to your scan bill class 
			startActivity(new Intent(getActivity(), CameraActivity.class));
		default:
			break;
		}
	}
	
	private void saveExpense() {
		int id = expense.getId();
		Main context = (Main) getActivity();
		
		String newTitle = editName.getText().toString();
		
		String newDate = editDate.getText().toString();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		try {
			sdf.parse(newDate);
		} catch (ParseException e) {
			context.makeToast(newDate + " is not a valid date! (Format: MM/dd/yyyy)");
			return;
		}
		double newAmount = Double.parseDouble(editAmount.getText().toString());
		String imgPath = null;
		String imgTitle = null;
		
		if (hasImg) {
			imgPath = receiptImgPath;
			imgTitle = receiptImgPath;
		}
		
		DBManager dbManager = new DBManager(context);
		// ID is -1 if expense was just created
		if (id < 0) {
			dbManager.addExpense(newAmount, newDate, newTitle, null, imgTitle, imgPath);
		} else {
			//sharedID = -1 for now
			dbManager.editExpense(id, -1, newAmount, newDate, newTitle, null, imgTitle, imgPath);
		}
		
		context.makeToast("Changes saved");
		context.goBack();
	}
	
	private void deleteExpense() {
		Main context = (Main) getActivity();
		DBManager dbManager = new DBManager(context);
		
		dbManager.deleteExpense(expense.getId());
		
		context.goBack();
	}
	
	public static void setDataFromCam(String title, String date, double amount, String imgPath)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		Calendar calendar = Calendar.getInstance();
		
		if(title!="") camExpense.setTitle(title);
		else camExpense.setTitle("expense");
		
		if(date!="" && date!="Couldn't detect amount") camExpense.setDate(date);
		else camExpense.setDate(sdf.format(calendar.getTime()));
		
		camExpense.setAmount(amount);
		
		if(imgPath!=null) {
			receiptImgPath = imgPath;
			receiptImg = BitmapUtils.createSampledBitmap(receiptImgPath, 1);
			
			hasImg = true;
		}
		
		cameFlag = true;	
	}
	
	private void zoomImgFromThumb (final View thumbView) {
		
		//Retrieve Bitmap more expanded view
		if(receiptImgPath == null || !hasImg) return;		
		Bitmap expanededReceiptImg = BitmapUtils.createSampledBitmap(receiptImgPath, 1);
		
		final ImageView expandedImageView = (ImageView) getActivity().findViewById(R.id.expanded_image);
		expandedImageView.setImageBitmap(expanededReceiptImg);
		
		if(mCurAnimator != null) mCurAnimator.cancel();					
				
		//Calculate the starting and ending bounds for zoomed-in image
		final Rect startBounds = new Rect();
	    final Rect finalBounds = new Rect();
	    final Point globalOffset = new Point();
	    
	    thumbView.getGlobalVisibleRect(startBounds); //Start bounds are the global visible rect of the thumbnail
	    getActivity().findViewById(R.id.fragmentShareExpense_container).getGlobalVisibleRect(finalBounds, globalOffset); //final bounds are the global visible rect of the container view
	    //Container view's offset is the origin for the bounds
	    startBounds.offset(-globalOffset.x, -globalOffset.y);
	    finalBounds.offset(-globalOffset.x, -globalOffset.y);
	    
	    //Calculate the start scaling factor
	    //Using "ceterCrop", adjusting the start bounds to be the same aspect ratio as the final bounds
	    float startScale;
	    if ((float) finalBounds.width() / finalBounds.height()
	            > (float) startBounds.width() / startBounds.height()) {
	        //Extend start bounds horizontally
	        startScale = (float) (startBounds.height() / finalBounds.height());
	        float startWidth = startScale * finalBounds.width();
	        float deltaWidth = (startWidth - startBounds.width()) / 2;
	        startBounds.left -= deltaWidth;
	        startBounds.right += deltaWidth;
	    } else {
	        //Extend start bounds vertically
	        startScale = (float) (startBounds.width() / finalBounds.width());
	        float startHeight = startScale * finalBounds.height();
	        float deltaHeight = (startHeight - startBounds.height()) / 2;
	        startBounds.top -= deltaHeight;
	        startBounds.bottom += deltaHeight;
	    }
	    
	    thumbView.setAlpha(0f);//Hide the thumbnail
	    expandedImageView.setVisibility(View.VISIBLE);//Show the zoomed-in image
	    
	    //Set the pivot point for SCALE_X and SCALE_Y transformations to the topLeft corner of the zoomed-in view
	    //(Default is the center of the view)
	    //expandedImageView.setPivotX(0f);
	    //expandedImageView.setPivotY(0f);
	    
	    //Construct and run the parallel animation of the four translation and scale properties
	    //(X, Y, SCALE_X, SCALE_Y)
	    float endScale = 0.9f; //Scale of the zoomed-in image relative to the container
	    AnimatorSet set = new AnimatorSet();
	    set
	            .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left, finalBounds.left))
	            .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
	            .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, endScale))
	            .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, endScale));
	    set.setDuration(mShortAnimDur);
	    set.setInterpolator(new DecelerateInterpolator());
	    set.addListener(new AnimatorListenerAdapter() {
	        @Override
	        public void onAnimationEnd(Animator animation) {
	            mCurAnimator = null;
	        }
	        @Override
	        public void onAnimationCancel(Animator animation) {
	        	mCurAnimator = null;
	        }
	    });
	    set.start();
	    mCurAnimator = set;
	    
	    //Upon clicking the zoomed-in image, it should zoom back down to the original
	    final float startScaleFinal = startScale;
	    
	    /*//For press and hold
	     * expandedImageView.setOnLongClickListener(new OnLongClickListener() {
			@Override
		    public boolean onLongClick(View theView) {
		    */
	    expandedImageView.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View view) {

	            if (mCurAnimator != null) mCurAnimator.cancel();

	            //Animate the four positioning/scaling properties in parallel, back to the original
	            AnimatorSet set = new AnimatorSet();
	            set			.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
	                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,startBounds.top))
	                        .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
	                        .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
	            set.setDuration(mShortAnimDur);
	            set.setInterpolator(new DecelerateInterpolator());
	            set.addListener(new AnimatorListenerAdapter() {
	                @Override
	                public void onAnimationEnd(Animator animation) {
	                    thumbView.setAlpha(1f);
	                    expandedImageView.setVisibility(View.GONE);
	                    mCurAnimator = null;
	                }

	                @Override
	                public void onAnimationCancel(Animator animation) {
	                    thumbView.setAlpha(1f);
	                    expandedImageView.setVisibility(View.GONE);
	                    mCurAnimator = null;
	                }
	            });
	            set.start();
	            mCurAnimator = set;
	            
	            //return true;
	        }
	    });
	}
	
}
