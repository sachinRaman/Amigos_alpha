package com.amigos.sindhusha.Values;

import android.graphics.Color;

import com.cunoraz.tagview.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Sachin on 8/1/2017.
 */

public class PeevesList {
    public final static String[] petPeeves = new String[]{"People",
            "Crowd",
            "Politics",
            "Philosophy",
            "Smoking",
            "Procrastination",
            "Overly Extroverts",
            "Overly Introverts",
            "Lying",
            "Hashtags",
            "Biting nails",
            "Slow walkers",
            "Misspelled signs",
            "Misspelling my name",
            "Fitness freaks",
            "K",
            "Hmm",
            "Social media",
            "PDA",
            "Pets",
            "Noisy Eaters",
            "Driving slow",
            "Sober people",
            "Drunk people",
            "Boss",
            "Fat people",
            "Kids",
            "Selfies",
            "Distraction",
            "Loud people",
            "No sense of parking",
            "No sense of public property",
            "Dating apps",
            "Pedestrians",
            "Animals",
            "Vegetarians",
            "Unpunctuality",
            "Hard-work",
            "Speed Breakers",
            "Overperfumed",
            "Clingy people",
            "Animal Cruelty",
            "Messy Toilets",
            "Personal Space",
            "Whiny people",
            "Bad Body Odour",
            "Bad Grammar",
            "Duck face"};

    public static ArrayList<String> getAllInterests(){
        HashMap<String,ArrayList<String>> prefTags = new HashMap<String, ArrayList<String>>();

        ArrayList<String> business = new ArrayList<String>(Arrays.asList("Entrepreneurship", "Management","Marketing","Strategy"));
        ArrayList<String> arts = new ArrayList<String>(Arrays.asList("Painting", "Sketching","Writing","Photography"));
        ArrayList<String> entertainment = new ArrayList<String>(Arrays.asList("Music", "Theatre","Dance","Storytelling","Movies","Comedy"));
        ArrayList<String> sports = new ArrayList<String>(Arrays.asList("Cricket", "Tennis","Football","Chess","Athletics"));
        ArrayList<String> music = new ArrayList<String>(Arrays.asList("EDM", "Rock","Hip Hop","Country Music","Pop Music","Rap","Instrumental"));
        ArrayList<String> lifestyle = new ArrayList<String>(Arrays.asList("Books", "Fashion","Food","Travel","Adventure"));
        ArrayList<String> psychology = new ArrayList<String>(Arrays.asList("Philosophy", "Psychoanalysis","Cognition","Sociology"));
        ArrayList<String> science = new ArrayList<String>(Arrays.asList("Astronomy", "Geoscience","Biology"));
        ArrayList<String> technology = new ArrayList<String>(Arrays.asList("Artificial Intelligence", "Cryptocurrency","Programming"));

        prefTags.put("business",business);
        prefTags.put("arts",arts);
        prefTags.put("entertainment",entertainment);
        prefTags.put("sports",sports);
        prefTags.put("music",music);
        prefTags.put("lifestyle",lifestyle);
        prefTags.put("psychology",psychology);
        prefTags.put("science",science);
        prefTags.put("technology",technology);

        ArrayList<String> interests = new ArrayList<String>();

        Iterator it = prefTags.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            for (String s:(ArrayList<String>)pair.getValue()){
                interests.add(s);
            }
            it.remove();
        }

        return interests;
    }

    public static ArrayList<String> getAllLifestyleInterests(){
        ArrayList<String> lifestyle = new ArrayList<String>(Arrays.asList("Fashion",
                "Food",
                "Travel",
                "Adventure",
                "TV series",
                "Anime and cartoons",
                "Astrology",
                "Chess",
                "Food",
                "Gaming",
                "Poker",
                "Psychology",
                "Tattoo"));
        return lifestyle;
    }

    public static ArrayList<String> getAllArtsInterests(){
        ArrayList<String> arts = new ArrayList<String>(Arrays.asList("Painting",
                "Sketching",
                "Writing",
                "Photography",
                "Drawing",
                "Poetry"));
        return arts;
    }

    public static ArrayList<String> getAllEntertainmentInterests(){
        ArrayList<String> arts = new ArrayList<String>(Arrays.asList("Music",
                "Theatre",
                "Dance",
                "Storytelling",
                "Movies",
                "Comedy"));
        return arts;
    }

    public static ArrayList<String> getAllBusinessInterests(){
        ArrayList<String> arts = new ArrayList<String>(Arrays.asList("Entrepreneurship",
                "Management",
                "Marketing",
                "Strategy"));
        return arts;
    }

    public static ArrayList<String> getAllSportsInterests(){
        ArrayList<String> arts = new ArrayList<String>(Arrays.asList("Cricket",
                "Tennis",
                "Football",
                "Badminton",
                "Athletics",
                "Basketball",
                "Bowling",
                "Golf",
                "Skating" ,
                "Vollyeball"));
        return arts;
    }

    public static ArrayList<String> getAllMusicInterests(){
        ArrayList<String> arts = new ArrayList<String>(Arrays.asList("EDM",
                "Rock",
                "Hip Hop",
                "Pop",
                "Rap",
                "Instrumental"));
        return arts;
    }

    public static ArrayList<String> getAllTechnologyInterests(){
        ArrayList<String> arts = new ArrayList<String>(Arrays.asList("Artificial Intelligence",
                "Crpytocurrency",
                "Programming" ,
                "Blockchain",
                "Space travel",
                "Astronomy"));
        return arts;
    }
}
