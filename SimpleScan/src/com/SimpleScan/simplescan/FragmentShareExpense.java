package com.SimpleScan.simplescan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
import android.graphics.BitmapFactory;
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
	private static boolean viewExpanded;
	
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
		newExpense.setAmount(0.00f);
		newExpense.setDate(sdf.format(calendar.getTime()));
		newExpense.setTitle("expense");
		DBManager dbManager = new DBManager(context);
		dbManager.addExpense(
				newExpense.getAmount(), 
				newExpense.getDate(), 
				newExpense.getTitle(), 
				newExpense.getCategory(), 
				newExpense.getImageTitle(), 
				newExpense.getImagePath()
				);
		
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
		setUpDatePicker(v);
		setUpCategory(v);		
		
		hasImg = false;
		viewExpanded = false;
		
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
	
	private void setUpEditExpense(View v) {
		// Set the default Image
		receiptImageView = (ImageView) v.findViewById(R.id.SE_im);
		
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
		ImageView ExpenseImg = (ImageView)v.findViewById(R.id.SE_im);
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
	
	private void setUpDatePicker(View v) {
		final Calendar calendar = Calendar.getInstance();
		
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
		editDate.setOnClickListener(new View.OnClickListener() {
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
			if(CameraUtils.checkCameraHardware(getActivity())) startActivity(new Intent(getActivity(), CameraActivity.class));
			else Toast.makeText(getActivity(), "Camera is not supported on your device", Toast.LENGTH_LONG).show();
			break;
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
		
		DBManager dbManager = new DBManager(context);
		dbManager.editExpense(id, newAmount, newDate, newTitle, null, null);
		
		context.makeToast("Changes saved");
	}
	
	private void deleteExpense() {
		// Does nothing
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
			BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inSampleSize = 6; //down-sampling the image
			receiptImg = BitmapFactory.decodeFile(receiptImgPath, options);
			
			hasImg = true;
		}
		
		cameFlag = true;
		
	}
	
	private void zoomImgFromThumb (final View thumbView) {
		
		if(receiptImgPath == null || !hasImg) return;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
		Bitmap expanededReceiptImg = BitmapFactory.decodeFile(receiptImgPath, options);
		
		if(mCurAnimator != null) mCurAnimator.cancel();				
	
		final ImageView expandedImageView = (ImageView) getActivity().findViewById(R.id.expanded_image);
		expandedImageView.setImageBitmap(expanededReceiptImg);
		
		final Rect startBounds = new Rect();
	    final Rect finalBounds = new Rect();
	    final Point globalOffset = new Point();
	    
	    thumbView.getGlobalVisibleRect(startBounds);
	    getActivity().findViewById(R.id.fragmentShareExpense_container).getGlobalVisibleRect(finalBounds, globalOffset);
	    startBounds.offset(-globalOffset.x, -globalOffset.y);
	    finalBounds.offset(-globalOffset.x, -globalOffset.y);
	    
	    float startScale;
	    if ((float) finalBounds.width() / finalBounds.height()
	            > (float) startBounds.width() / startBounds.height()) {
	        // Extend start bounds horizontally
	        startScale = (float) startBounds.height() / finalBounds.height();
	        float startWidth = startScale * finalBounds.width();
	        float deltaWidth = (startWidth - startBounds.width()) / 2;
	        startBounds.left -= deltaWidth;
	        startBounds.right += deltaWidth;
	    } else {
	        // Extend start bounds vertically
	        startScale = (float) startBounds.width() / finalBounds.width();
	        float startHeight = startScale * finalBounds.height();
	        float deltaHeight = (startHeight - startBounds.height()) / 2;
	        startBounds.top -= deltaHeight;
	        startBounds.bottom += deltaHeight;
	    }
	    
	    thumbView.setAlpha(0f);
	    expandedImageView.setVisibility(View.VISIBLE);
	    
	    expandedImageView.setPivotX(0f);
	    expandedImageView.setPivotY(0f);
	    
	    AnimatorSet set = new AnimatorSet();
	    set
	            .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
	                    startBounds.left, finalBounds.left))
	            .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
	                    startBounds.top, finalBounds.top))
	            .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
	            startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
	                    View.SCALE_Y, startScale, 1f));
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
	    
	    final float startScaleFinal = startScale;
	    expandedImageView.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View view) {
	            if (mCurAnimator != null) mCurAnimator.cancel();

	            AnimatorSet set = new AnimatorSet();
	            set.play(ObjectAnimator
	                        .ofFloat(expandedImageView, View.X, startBounds.left))
	                        .with(ObjectAnimator
	                                .ofFloat(expandedImageView, 
	                                        View.Y,startBounds.top))
	                        .with(ObjectAnimator
	                                .ofFloat(expandedImageView, 
	                                        View.SCALE_X, startScaleFinal))
	                        .with(ObjectAnimator
	                                .ofFloat(expandedImageView, 
	                                        View.SCALE_Y, startScaleFinal));
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
	        }
	    });
	}
}
