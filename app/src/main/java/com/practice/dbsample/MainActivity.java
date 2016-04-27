package com.practice.dbsample;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    CardImage selected_1, selected_2;
    int mScore = 0;
    TextView mScoreView;
    int mGameCounter = 0;
    int IMG_WIDTH = 80, IMG_HEIGHT = 100;

    int image_ids[] = {
            R.drawable.colour1, R.drawable.colour2,
            R.drawable.colour3, R.drawable.colour4,
            R.drawable.colour5, R.drawable.colour6,
            R.drawable.colour7, R.drawable.colour8
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //To show default score.
        mScoreView = (TextView) findViewById(R.id.score);
        mScoreView.setText("Score : "+mScore);

        //Onclick listener for the score board.
        Button scoreBoard = (Button) findViewById(R.id.high_score);
        scoreBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScoresActivity.class);
                startActivity(intent);
                finish();
            }
        });

        showNewBoard();
//        showCongratsAlert();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_game) {
            ((RelativeLayout)(findViewById(R.id.game_board))).removeAllViews();
            showNewBoard();
            selected_1 = null;
            selected_2 = null;
            mScore = 0;
            mGameCounter = 0;
            mScoreView.setText("Score : "+mScore);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * To show new GameBoard.
     * This method will get new board and display on screen.
     * */
    private void showNewBoard() {
        final RelativeLayout gameBoard1 = (RelativeLayout)findViewById(R.id.game_board);
        gameBoard1.post(new Runnable() {
            @Override
            public void run() {
                //Log.i(getClass().getSimpleName(),  "game board width in pixels :: "+gameBoard1.getWidth()+"  height in pixels :: "+gameBoard1.getHeight());
                TableLayout gameBoard = setupNewBoard(gameBoard1.getWidth(), gameBoard1.getHeight());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, R.id.high_score);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                gameBoard.setLayoutParams(params);

                ((RelativeLayout)(findViewById(R.id.game_board))).addView(gameBoard);
            }
        });
    }

    /**
     * Constructs the new game board by rearranging the card images.
     *
     * */
    private TableLayout setupNewBoard(int width, int height) {
        CardOnClickListener cardOnClickListener = new CardOnClickListener();

        int availableWidth = width;
        int availableHeight = height;
        double density = getDeviceDensity();
        while(!(availableWidth > (4 * IMG_WIDTH * density) && availableHeight > (4 * IMG_HEIGHT * density))) {
            density-=0.5;
            Log.i(getClass().getSimpleName(), "Modified density :: "+density);
        }

        int imageOrder[] = boardImgOrder();

        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setBackgroundColor(getResources().getColor(R.color.gameboard));
        tableLayout.setHorizontalGravity(Gravity.CENTER);
        tableLayout.setColumnShrinkable(0, true);
        tableLayout.setColumnShrinkable(1, true);
        tableLayout.setColumnShrinkable(2, true);
        tableLayout.setColumnShrinkable(3, true);

        for(int j =1,k=0; j<=4;j++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setHorizontalGravity(Gravity.CENTER);

            for(int i=1; i<=4; i++, k++) {
                CardImage cardImage = new CardImage(this);
                cardImage.setLayoutParams(new TableRow.LayoutParams((int)(IMG_WIDTH * density), (int)(IMG_HEIGHT * density)));
                cardImage.imgIndex = imageOrder[k];
                cardImage.setImageResource(R.drawable.card_bg);
                cardImage.setOnClickListener(cardOnClickListener);
                tableRow.addView(cardImage);
            }
            tableLayout.addView(tableRow);
        }
    return tableLayout;
    }

    /**
     * Constructs the card images order for new game board.
     * */
    private int[] boardImgOrder() {
        int id[] = new int[16];
        ArrayList<Integer> ids = new ArrayList<>();
        for(int i=0,j=1; i< 16;i++,j++) {
            ids.add(i, j);
            if(j == 8)
                j=0;
        }

        int randNumber;
        for(int i=15,j=0; i>0; i--,j++) {
            randNumber = (int) new Random().nextInt(1000)%i;
            id[j] = ids.get(randNumber);
            ids.remove(randNumber);
        }
        id[15] = ids.get(0);

        Log.i(getClass().getSimpleName(), Arrays.toString(id));

        return id;
    }

    /**
     * Card image onClick listener.
     * Game logic is been implemented here.
     * */
    class CardOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.i(getClass().getSimpleName(), "card click()");

            if(selected_1 == null) {
                selected_1 = (CardImage) v;
                selected_1.lock = true;
                flipCardImage(selected_1);
            } else if(selected_2 == null && selected_1 != v) {
                selected_2 = (CardImage) v;
                selected_2.lock = true;
                if(selected_1.imgIndex == selected_2.imgIndex) {
                    selected_1.setOnClickListener(null);
                    selected_2.setOnClickListener(null);
                    mScore+=2;
                    mGameCounter++;
                    if(mGameCounter == 8)
                        showCongratsAlert();
                } else if(selected_1 != null && selected_2 != null){
                    mScore-=1;
                    selected_1.lock = false;
                    selected_2.lock = false;
                }
                flipCardImage(selected_2);
                mScoreView.setText("Score : "+mScore);
            } else if(selected_1 != null && selected_2 != null && selected_2 != v && selected_1 != v){
                Log.i(getClass().getSimpleName(), "selected_1 : "+selected_1.lock);
                Log.i(getClass().getSimpleName(), "selected_2 : "+selected_2.lock);
                if(selected_1.lock == false)
                    flipCardImage(selected_1);
                if(selected_2.lock == false)
                    flipCardImage(selected_2);
                selected_1 = null;
                selected_2 = null;
                selected_1 = (CardImage) v;
                selected_1.lock = true;
                flipCardImage(selected_1);
            }
        }
    }

    /**
     * Flips the card image based on the card state.
     * */
    private void flipCardImage(CardImage cardImage) {
        if(cardImage.flip == false) {
            cardImage.flip = true;
            leftOut(cardImage);
        } else {
            cardImage.flip = false;
            rightOut(cardImage);
        }
    }

    /**
     * Animation util for left out animation.
     * */
    private void leftOut(final View cardView) {
        final AnimatorSet leftOut = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                R.animator.card_flip_left_out);
        leftOut.setTarget(cardView);

        final AnimatorSet leftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                R.animator.card_flip_left_in);
        leftIn.setTarget(cardView);

        leftOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ((CardImage)cardView).setImageResource(image_ids[((CardImage)cardView).imgIndex-1]);
                leftIn.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        leftOut.start();
    }

    /**
     * Animation util for right out animation.
     * */
    private void rightOut(final View cardView) {
        final AnimatorSet rightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                R.animator.card_flip_right_out);
        rightOut.setTarget(cardView);

        final AnimatorSet rightIn = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                R.animator.card_flip_right_in);
        rightIn.setTarget(cardView);

        rightOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ((CardImage)cardView).setImageResource(R.drawable.card_bg);
                rightIn.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        rightOut.start();
    }

    /**
     * Show the success message and takes the user name as input to show in score board.
     * */
    private void showCongratsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Congratulations. your score is "+mScore+".");

        // Set up the input
        LayoutInflater inflater = (LayoutInflater)   MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_congrats, null);
        final EditText userName = (EditText) view.findViewById(R.id.username);
        final TextView exceptionDesc = (TextView) view.findViewById(R.id.dialog_congrats_tv);

        builder.setView(view);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBHelper myDb = new DBHelper(MainActivity.this);
                myDb.insertRecord(userName.getText().toString(), mScore);

                Intent intent = new Intent(MainActivity.this, ScoresActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //builder.show();
        final AlertDialog dialog = builder.create();
        dialog.show();

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String enteredStr = s.toString();
                if(enteredStr.matches("^[a-zA-Z]+[0-9]*$") && enteredStr.length() >= 3 && enteredStr.length() <= 8) {
                    exceptionDesc.setVisibility(View.INVISIBLE);
                    dialog.getButton(AlertDialog.BUTTON1).setEnabled(true);
                } else {
                    dialog.getButton(AlertDialog.BUTTON1).setEnabled(false);

                    exceptionDesc.setText("Name should be start with alphabet and length should be in-between 3 to 8.");
                    exceptionDesc.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * To get the device density which will help to fit the game board in screen appropriately.
     * */
    private double getDeviceDensity() {
        double density= getResources().getDisplayMetrics().density;
        Log.i(getClass().getSimpleName(), "Devcie density :: "+density);
        return density;
    }
}
