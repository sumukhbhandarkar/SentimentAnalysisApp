package dev.sumukh.sentimentanalysisapp;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {

    private static String happy = "glad, happy, accidental, advantageous, appropriate, apt, auspicious, befitting, casual, convenient, correct, effective, efficacious, enviable, favorable, felicitous, fitting, fortunate, incidental, just, meet, nice, opportune, promising, proper, propitious, providential, right, satisfactory, seasonable, successful, suitable, timely, well-timed";
    private String lucky = "dream, win, lucky, advantageous, bright, favorable, felicitous, fortunate, golden, halcyon, happy, hopeful, lucky, opportune, promising, propitious, prosperous, rosy, timely, well-timed";
    private String sad = "sad, bad, calamitous, dark, dejecting, deplorable, depressing, disastrous, discomposing, discouraging, disheartening, dismal, dispiriting, dreary, funereal, grave, grievous, hapless, heart-rending, joyless, lachrymose, lamentable, lugubrious, melancholic, miserable, moving, oppressive, pathetic, pitiable, pitiful, poignant, regrettable, saddening, serious, shabby, sorry, tear-jerking, tearful, tragic, unhappy, unsatisfactory, upsetting, wretched";
    private String angry = "angry, affronted, annoyed, antagonized, bitter, chafed, choleric, convulsed, cross, displeased, enraged, exacerbated, exasperated, ferocious, fierce, fiery, fuming, furious, galled, hateful, heated, hot, huffy, ill-tempered, impassioned, incensed, indignant, inflamed, infuriated, irascible, irate, ireful, irritable, irritated, maddened, nettled, offended, outraged, piqued, provoked, raging, resentful, riled, sore, splenetic, storming, sulky, sullen, tumultous/tumultuous, turbulent, uptight, vexed, wrathful";
    private String tired = "tired, annoy, bore, burn out, bush, collapse, crawl, debilitate, deject, depress, disgust, dishearten, dispirit, displease, distress, drain, droop, drop, enervate, ennui, exasperate, fag, fail, faint, fatigue, flag, fold, give out, go stale, grow weary, harass, irk, irritate, jade, nauseate, overburden, overstrain, overtax, overwork, pain, pall, peter out, poop out, prostrate, put to sleep, sap, sicken, sink, strain, tax, vex, weaken, wear, wear down, wear out, wilt, worry, yawn";
    private String unhappy = "bleak, bleeding, blue, bummed out, cheerless, crestfallen, dejected, depressed, despondent, destroyed, disconsolate, dismal, dispirited, down and out, down in the mouth, down, downbeat, downcast, dragged, dreary, gloomy, grim, heavy-hearted, hurting, in a blue funk, in pain, in the dumps, let-down, long-faced, low, melancholy, mirthless, miserable, mournful, oppressive, put away, ripped, saddened, sorrowful, sorry, teary, troubled";
    private String stressed = "loss, lost, stress, accent, belabor, dwell, feature, harp, headline, italicize, emphasis, emphatic, repeat, spot, spotlight, underline, underscore";
    private String pleasure = "pleasure, amusement, bliss, comfort, contentment, delectation, diversion, ease, enjoyment, entertainment, felicity, flash*, fruition, game, gladness, gluttony, gratification, gusto, hobby, indulgence, joie de vivre, joy, joyride, kicks, luxury, primrose path, recreation, relish, revelry, satisfaction, seasoning, self-indulgence, solace, spice, thrill, titillation, turn-on, velvet, zest";
    private String stopwords = "a, an, the, this, these, I, am, was, were";

    static List<String> happy_array = Arrays.asList(happy.split("\\s*,\\s*"));
    List<String> lucky_array = Arrays.asList(lucky.split("\\s*,\\s*"));
    List<String> sad_array = Arrays.asList(sad.split("\\s*,\\s*"));
    List<String> angry_array = Arrays.asList(angry.split("\\s*,\\s*"));
    List<String> tired_array = Arrays.asList(tired.split("\\s*,\\s*"));
    List<String> unhappy_array = Arrays.asList(unhappy.split("\\s*,\\s*"));
    List<String> stressed_array = Arrays.asList(stressed.split("\\s*,\\s*"));
    List<String> pleasure_array = Arrays.asList(pleasure.split("\\s*,\\s*"));
    List<String> stopwords_array = Arrays.asList(stopwords.split("\\s*,\\s*"));

    public Double accuracy;
    public String polarity;
    public String phrase;
    EditText searchItem;
    Button searchButton, resetButton;
    ProgressBar progressBar;
    TableLayout output;
    TextView op_phrase;
    TextView op_polarity;
    TextView op_accuracy;

    int happy_score = 0, sad_score = 0, lucky_score = 0, angry_score = 0, tired_score = 0;
    int unhappy_score = 0, stressed_score = 0, pleasure_score = 0, neutral_score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.hide();
        output = (TableLayout)findViewById(R.id.output);
        output.setVisibility(GONE);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        progressBar.setVisibility(GONE);
        searchItem = (EditText)findViewById(R.id.search_item);
        searchButton = (Button)findViewById(R.id.search_button);
        op_phrase = (TextView)findViewById(R.id.phrase_txt);
        op_polarity = (TextView)findViewById(R.id.polarity_txt);
        op_accuracy = (TextView)findViewById(R.id.accuracy_txt);
        resetButton = findViewById(R.id.resetButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phrase = searchItem.getText().toString();
                phrase = phrase.toLowerCase();
                searchButton.setVisibility(GONE);
                progressBar.setVisibility(View.VISIBLE);
                output.setVisibility(View.VISIBLE);
                searchItem.setFocusable(false);
                cleanText();
            }
        });
    }

    public void resetButtonClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            searchItem.restoreDefaultFocus();
        }
        searchItem.setFocusable(true);
        searchItem.setText("");
        searchButton.setVisibility(View.VISIBLE);
        op_phrase.setText("Phrase:  ");
        op_polarity.setText("Polarity:  ");
        op_accuracy.setText("Accuracy:  ");

    }

    private void findPolarity(String phrase) {

        List polarity_phrase = Arrays.asList(phrase.split(" "));
        for (int i = 0; i < polarity_phrase.size(); i++) {
            if(happy_array.contains(polarity_phrase.get(i)))
                happy_score=happy_score+1;
            else if(sad_array.contains(polarity_phrase.get(i)))
                sad_score = sad_score + 1;
            else if(lucky_array.contains(polarity_phrase.get(i)))
                lucky_score = lucky_score + 1;
            else if(angry_array.contains(polarity_phrase.get(i)))
                angry_score = angry_score + 1;
            else if(tired_array.contains(polarity_phrase.get(i)))
                tired_score = tired_score + 1;
            else if(unhappy_array.contains(polarity_phrase.get(i)))
                unhappy_score = unhappy_score + 1;
            else if(stressed_array.contains(polarity_phrase.get(i)))
                stressed_score = stressed_score + 1;
            else if(pleasure_array.contains(polarity_phrase.get(i)))
                pleasure_score = pleasure_score + 1;
            else
                neutral_score = neutral_score + 1;
        }
        findLargestScore(happy_score, sad_score, lucky_score, angry_score, tired_score, unhappy_score, stressed_score, pleasure_score, neutral_score, phrase);
    }

    private void findLargestScore(int happy_score, int sad_score, int lucky_score, int angry_score, int tired_score, int unhappy_score, int stressed_score, int pleasure_score, int neutral_score, String phrase) {
        String largest = "";
        if(happy_score >= sad_score &&
                sad_score >= neutral_score &&
                happy_score >= lucky_score &&
                happy_score >= angry_score &&
                happy_score >= tired_score &&
                happy_score >= unhappy_score &&
                happy_score >= stressed_score &&
                happy_score >= pleasure_score) {
            op_polarity.append("Happy ");
            largest = "happy";
//            Toast.makeText(getApplicationContext(), String.valueOf(happy_score), Toast.LENGTH_LONG).show();
        }

        if(sad_score >= happy_score &&
                sad_score >= neutral_score &&
                sad_score >= lucky_score &&
                sad_score>= angry_score &&
                sad_score >= tired_score &&
                sad_score >= unhappy_score &&
                sad_score >= stressed_score &&
                sad_score >= pleasure_score) {
            op_polarity.append("Sad ");
            largest = "sad";
//            Toast.makeText(getApplicationContext(), String.valueOf(sad_score), Toast.LENGTH_LONG).show();
        }


        if(lucky_score >= happy_score &&
                lucky_score >= neutral_score &&
                lucky_score >= sad_score &&
                lucky_score>= angry_score &&
                lucky_score >= tired_score &&
                lucky_score >= unhappy_score &&
                lucky_score >= stressed_score &&
                lucky_score >= pleasure_score) {
            op_polarity.append("Lucky ");
            largest = "lucky";
//            Toast.makeText(getApplicationContext(), String.valueOf(lucky_score), Toast.LENGTH_LONG).show();
        }

        if(angry_score >= happy_score &&
                angry_score>= neutral_score &&
                angry_score >= sad_score &&
                angry_score>= lucky_score &&
                angry_score >= tired_score &&
                angry_score >= unhappy_score &&
                angry_score >= stressed_score &&
                angry_score >= pleasure_score) {
            op_polarity.append("Angry ");
            largest = "angry";
//            Toast.makeText(getApplicationContext(), String.valueOf(angry_score), Toast.LENGTH_LONG).show();
        }

        if(tired_score >= happy_score &&
                tired_score>= neutral_score &&
                tired_score >= sad_score &&
                tired_score>= lucky_score &&
                tired_score >= angry_score &&
                tired_score >= unhappy_score &&
                tired_score >= stressed_score &&
                tired_score >= pleasure_score) {
            op_polarity.append("Tired ");
            largest = "tired";
//            Toast.makeText(getApplicationContext(), String.valueOf(tired_score), Toast.LENGTH_LONG).show();
        }

        if(unhappy_score >= happy_score &&
                unhappy_score>= neutral_score &&
                unhappy_score >= sad_score &&
                unhappy_score>= lucky_score &&
                unhappy_score >= angry_score &&
                unhappy_score >= tired_score &&
                unhappy_score >= stressed_score &&
                unhappy_score >= pleasure_score) {
            op_polarity.append("Unhappy ");
            largest = "unhappy";
//            Toast.makeText(getApplicationContext(), String.valueOf(unhappy_score), Toast.LENGTH_LONG).show();
        }

        if(stressed_score >= happy_score &&
                stressed_score>= neutral_score &&
                stressed_score >= sad_score &&
                stressed_score>= lucky_score &&
                stressed_score >= angry_score &&
                stressed_score >= unhappy_score &&
                stressed_score >= tired_score &&
                stressed_score >= pleasure_score) {
            op_polarity.append("Stressed");
            largest = "stressed";
//            Toast.makeText(getApplicationContext(), String.valueOf(stressed_score), Toast.LENGTH_LONG).show();
        }

        if(pleasure_score >= happy_score &&
                pleasure_score>= neutral_score &&
                pleasure_score >= sad_score &&
                pleasure_score>= lucky_score &&
                pleasure_score >= angry_score &&
                pleasure_score >= unhappy_score &&
                pleasure_score >= stressed_score &&
                pleasure_score >= tired_score) {
            op_polarity.append("Pleasure");
            largest = "pleasure";
//            Toast.makeText(getApplicationContext(), String.valueOf(pleasure_score), Toast.LENGTH_LONG).show();
        }

        if(neutral_score >= happy_score &&
                neutral_score >= sad_score &&
                neutral_score >= lucky_score &&
                neutral_score>= angry_score &&
                neutral_score >= tired_score &&
                neutral_score >= unhappy_score &&
                neutral_score >= stressed_score &&
                neutral_score >= pleasure_score) {
            op_polarity.append("Neutral ");
            largest = "neutral";
//            Toast.makeText(getApplicationContext(), String.valueOf(neutral_score), Toast.LENGTH_LONG).show();
        }

        findAccuracy(largest, phrase, happy_score, sad_score, pleasure_score, neutral_score, lucky_score, angry_score, unhappy_score, stressed_score, tired_score);



    }

    private void findAccuracy(String largest, String phrase, int happy_score, int sad_score, int pleasure_score, int neutral_score, int lucky_score, int angry_score, int unhappy_score, int stressed_score, int tired_score) {
        if(largest == "happy") op_accuracy.append(String.valueOf((float) ((happy_score *100  / phrase.split(" ").length))));
        else if(largest == "sad") op_accuracy.append(String.valueOf((float) ((sad_score * 100  / phrase.split(" ").length))));
        else if(largest == "lucky") op_accuracy.append(String.valueOf((float) ((lucky_score * 100  / phrase.split(" ").length))));
        else if(largest == "angry") op_accuracy.append(String.valueOf((float) ((angry_score * 100 / phrase.split(" ").length))));
        else if(largest == "tired") op_accuracy.append(String.valueOf((float) ((tired_score * 100 / phrase.split(" ").length))));
        else if(largest == "unhappy") op_accuracy.append(String.valueOf((float) ((unhappy_score * 100 / phrase.split(" ").length))));
        else if(largest == "stressed") op_accuracy.append(String.valueOf((float) ((stressed_score * 100 / phrase.split(" ").length))));
        else if(largest == "pleasure") op_accuracy.append(String.valueOf((float) ((pleasure_score * 100 / phrase.split(" ").length))));
        else if(largest == "neutral") op_accuracy.append(String.valueOf((float) ((neutral_score * 100 / phrase.split(" ").length))));
        progressBar.setVisibility(GONE);
        op_accuracy.append(" %");
    }

    private void cleanText() {
        phrase = TextCleaning.punctuationRemoval(phrase);
        phrase = TextCleaning.removeStopWords(phrase);
        phrase = TextCleaning.nounRemoval(phrase);
        phrase = TextCleaning.caseEqualizer(phrase);
        op_phrase.append(phrase);
        findPolarity(phrase);
    }

    public void detailButtonClick(View v) {
        plotDetailGraph();
    }

    private void plotDetailGraph() {

        Intent intent = new Intent(MainActivity.this, GraphActivity.class);
        intent.putExtra("numValues", phrase.split(" ").length);
        intent.putExtra("happy_score", happy_score);
        intent.putExtra("sad_score", sad_score);
        intent.putExtra("pleasure_score", pleasure_score);
        intent.putExtra("neutral_score", neutral_score);
        intent.putExtra("unhappy_score", unhappy_score);
        intent.putExtra("lucky_score", lucky_score);
        intent.putExtra("tired_score", tired_score);
        intent.putExtra("angry_score", angry_score);
        intent.putExtra("stressed_score", stressed_score);
        startActivity(intent);

    }
}
