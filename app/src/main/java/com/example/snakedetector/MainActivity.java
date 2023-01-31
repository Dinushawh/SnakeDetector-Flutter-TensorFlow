package com.example.snakedetector;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snakedetector.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {

    TextView result, confidence,about,abc,confidencesText,aboutdata;
    ImageView imageView;
    Button picture;
    Button picture2;
    int imageSize = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        confidencesText = findViewById(R.id.confidencesText);
        abc = findViewById(R.id.classified);
        aboutdata =findViewById(R.id.aboutdata);
        result = findViewById(R.id.result);
        about = findViewById(R.id.aboutdata);
        confidence = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.button);
        picture2 = findViewById(R.id.gallery);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, 1);
                    } else {
                        //Request camera permission if we don't have it.
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                    }
                }
            }
        });

        picture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get image from gallery
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        });
    }
    public void classifyImage(Bitmap image){
        try {
            Model model = Model.newInstance(getApplicationContext());
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect  (4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues,0,image.getWidth(),0,0,image.getWidth(),image.getHeight());

            int pixel = 0;
            for(int i =0;i<imageSize;i++){
                for(int j = 0;j<imageSize;j++){
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF)*(1.f/255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF)*(1.f/255.f));
                    byteBuffer.putFloat((val >> 0xFF)*(1.f/255.f));
                }
            }
            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i =0;i< confidences.length;i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }

            }

            String[] classes = {"agkistrodon-contortrix",
                    "agkistrodon-piscivorus",
                    "coluber-constrictor",
                   "crotalus-atrox",
                    "crotalus-horridus",
                    "crotalus-ruber",
                    "crotalus-scutulatus",
                   "crotalus-viridis,",
                    "diadophis-punctatus",
                    "haldea-striatula",
                    "heterodon-platirhinos",
                    "lampropeltis-californiae",
                    "lampropeltis-triangulum",
                   "masticophis-flagellum",
                    "natrix-natrix",
                    "nerodia-erythrogaster",
                    "nerodia-fasciata",
                   "nerodia-rhombifer",
                    "nerodia-sipedon",
                   "opheodrys-aestivus",
                    "pantherophis-alleghaniensis",
                   "pantherophis-emoryi",
                    "pantherophis-guttatus",
                    "pantherophis-obsoletus",
                    "pantherophis-spiloides",
                    "pantherophis-vulpinus",
                   "pituophis-catenifer",
                    "rhinocheilus-lecontei",
                    "storeria-dekayi",
                    "storeria-occipitomaculata",
                    "thamnophis-elegans",
                    "thamnophis-marcianus",
                    "thamnophis-proximus",
                    "thamnophis-radix",
                    "thamnophis-sirtalis"};


            String [] Description = {
                    "Agkistrodon contortrix, commonly known as the copperhead, is a venomous species of pit viper found in the southeastern United States. They are known for their distinctive copper-colored head and the hourglass-shaped crossbands on their bodies, which can vary in color from light brown to a dark reddish-brown. They typically grow to around 2-3 feet in length and can be found in a variety of habitats, including forests, swamps, and rocky hillsides. Copperheads are not aggressive and bites are rare, but their venom can cause significant pain, swelling, and tissue damage. Copperheads are not considered as a threat for human and most of the time they do not bite unless they feel threatened.",
                    "Agkistrodon piscivorus, commonly known as the cottonmouth or water moccasin, is a venomous species of pit viper found in the southeastern United States. They are known for their distinctive dark-colored bodies and their habit of opening their mouths wide when threatened, revealing the white interior, which is where they get their common name \"cottonmouth\". They typically grow to around 2-4 feet in length and can be found near water, such as swamps, marshes, and streams. They are considered more aggressive and more dangerous than copperheads. Cottonmouth venom can cause severe pain, swelling, tissue damage and in some cases, even death if left untreated. As with most venomous snakes, it's best to avoid them if possible, but if you encounter one, it is important to keep a safe distance and give them a way to escape.",
                    "Coluber constrictor, commonly known as the eastern racers, is a species of nonvenomous snake found in North America. They are known for their slender build, smooth scales, and distinctive coloration, which can vary from solid blue-gray to green with black and white bars. They typically grow to around 3-5 feet in length and can be found in a variety of habitats, including forests, fields, and deserts. Eastern racers are fast moving and active during the day and are known to be curious and unafraid of humans, they are not considered dangerous to humans and typically do not bite unless threatened. They are known to be beneficial to the ecosystem as they help control rodent populations.",
                    "Crotalus atrox, commonly known as the western diamondback rattlesnake, is a venomous species of pit viper found in the southwestern United States and Mexico. They are known for their diamond-shaped crossbands on their bodies, which can vary in color from light brown to a dark reddish-brown, as well as the distinctive rattle at the end of their tail. They typically grow to around 3-5 feet in length and can be found in a variety of habitats, including deserts, rocky hillsides, and scrublands. Western diamondback rattlesnakes are considered dangerous and can cause serious injury or death if bitten. Their venom can cause severe pain, swelling, tissue damage, and in some cases, even death if left untreated. As with most venomous snakes, it's best to avoid them if possible, but if you encounter one, it is important to keep a safe distance and give them a way to escape.",
                    "Crotalus horridus, commonly known as the timber rattlesnake, is a venomous species of pit viper found in the eastern United States. They are known for their distinctive coloration, which can vary from solid black to yellowish-brown with darker crossbands, as well as the distinctive rattle at the end of their tail. They typically grow to around 3-5 feet in length and can be found in a variety of habitats, including forests, rocky hillsides, and swamps. Timber rattlesnakes are considered dangerous and can cause serious injury or death if bitten. Their venom can cause severe pain, swelling, tissue damage, and in some cases, even death if left untreated. As with most venomous snakes, it's best to avoid them if possible, but if you encounter one, it is important to keep a safe distance and give them a way to escape. It's also worth noting that Timber Rattlesnake is considered as a threatened species and it's protected by law in most of the states.",
                    "Crotalus ruber, also known as the red diamond rattlesnake, is a venomous species of pit viper that is native to western North America. They have a distinct reddish-orange coloration with dark diamond shaped blotches on their back. They are found in a variety of habitats, including deserts, chaparral, and woodlands. They are known to be active during the day and feed on small mammals and reptiles. They are venomous, and a bite from a red diamond rattlesnake can cause severe symptoms such as pain, swelling, and tissue damage. They are considered as a threatened species and their population is declining.",
                    "Crotalus scutulatus, commonly known as the Mojave rattlesnake, is a venomous species of pit viper found in the southwestern United States and northern Mexico. They are known for their distinctive coloration, which can vary from pale gray to yellowish-brown with darker crossbands, as well as the distinctive rattle at the end of their tail. They typically grow to around 3-4 feet in length. They can be found in a variety of habitats, including deserts, rocky hillsides, and scrublands. The venom of the Mojave rattlesnake is considered to be one of the most potent of all North American venomous snakes, capable of causing severe symptoms such as pain, swelling, and tissue damage. As with most venomous snakes, it's best to avoid them if possible but if encountered, it is important to keep a safe distance and give them a way to escape.",
                    "Crotalus viridis, commonly known as the prairie rattlesnake, is a venomous species of pit viper found in the western United States and Canada. They are known for their distinctive coloration, which can vary from pale gray to yellowish-brown with darker crossbands, as well as the distinctive rattle at the end of their tail. They typically grow to around 3-4 feet in length and can be found in a variety of habitats, including grasslands, deserts, and rocky hillsides. The venom of the prairie rattlesnake is less potent than the venom of some other species of rattlesnake, but it can still cause severe symptoms such as pain, swelling, and tissue damage. As with most venomous snakes, it's best to avoid them if possible but if encountered, it is important to keep a safe distance and give them a way to escape.",
                    "Diadophis punctatus, commonly known as the ring-necked snake, is a species of small, nonvenomous snake found in North America. They are known for their distinctive coloration, which can vary from a reddish-brown to a dark gray or black, with a bright yellow or orange ring around their neck. They typically grow to around 1-2 feet in length and can be found in a variety of habitats, including forests, fields, and deserts. They are often found under rocks, logs, and other debris, where they hunt for small invertebrates such as worms and slugs. Ring-necked snakes are not considered dangerous to humans and typically do not bite unless threatened. They are known to be beneficial to the ecosystem as they help control invertebrate populations.",
                    "Haldea striatula is a small and slender snake-like lizard species that is native to the Western Ghats of India. They are known for their brown or dark gray coloration, with darker stripes on their backs and sides. They are typically around 20 cm in total length. They are found in a variety of habitats, including forested areas and can be found in elevation range of 600-1300 m above sea level. They are active during the day and feeds on insects, small arthropods and other small invertebrates. They are considered as a rare species, and are protected under Indian Wildlife Protection Act, 1972, due to their limited distribution and potential threats to their habitat.",
                    "Heterodon platirhinos, commonly known as the eastern hog-nosed snake, is a species of nonvenomous snake found in eastern North America. They are known for their distinctive upturned snout, which they use to dig for food, and their coloration, which can vary from gray to brown or black with white or yellow spots. They typically grow to around 2-3 feet in length and can be found in a variety of habitats, including forests, fields, and wetlands. Eastern hog-nosed snakes are not considered dangerous to humans and typically do not bite unless threatened. They are known to be beneficial to the ecosystem as they help control invertebrate populations. When threatened, they will often play dead before flipping over and showing their belly, and sometimes hiss and spread their neck like a cobra to intimidate predators.",
                    "Lampropeltis californiae, commonly known as the California mountain kingsnake, is a nonvenomous species of snake found in western North America, specifically in California and adjacent areas. They are known for their distinctive coloration, which can vary from black or dark brown with white or yellow crossbands or spots, and a smooth scales. They typically grow to around 3-4 feet in length and can be found in a variety of habitats, including forests, deserts, and rocky hillsides. California mountain kingsnakes are not considered dangerous to humans and typically do not bite unless threatened. They are known to be beneficial to the ecosystem as they help control rodent populations. They are also popular among snake enthusiasts due to their beautiful coloration and docile nature, many are kept as pets.",
                    "Lampropeltis triangulum, commonly known as the eastern milk snake, is a species of nonvenomous snake found in North America. They are typically between 2 and 4 feet in length and have a distinctive pattern of red, black, and yellow or white bands on their bodies. They are known for their docile nature and are often kept as pets. They primarily feed on small mammals and other small reptiles.",
                    "Masticophis flagellum, commonly known as the coachwhip, eastern coachwhip, or red racer, is a species of nonvenomous snake found in North America. They are typically between 4 and 8 feet in length, and are known for their slender bodies and long tails. They are typically colored black, brown, or gray, with a reddish or orange belly. They are fast-moving and active during the day, and primarily feed on small mammals, lizards, and birds. They are also known for their aggressive behavior and are not recommended as pets.",
                    "Natrix natrix, commonly known as the European grass snake, is a species of nonvenomous snake found in Europe, North Africa and Asia. They are typically between 1 and 1.5 meters in length, and are known for their smooth, cylindrical body and a long, pointed head. They are typically green or brown in color, with a yellow or white belly. They are semi-aquatic and are often found near water, where they hunt for fish, amphibians and small mammals. They are not aggressive and when threatened, they tend to hide or escape in the water rather than bite.",
                    "Nerodia erythrogaster, commonly known as the plainbelly water snake, is a species of nonvenomous snake found in the southeastern United States. They are typically between 2 and 4 feet in length, and are known for their heavy bodies, smooth scales and a plain belly. They are typically dark brown or black in color, with a reddish-orange or copper-colored belly. They are semi-aquatic and are often found near water, where they hunt for fish, amphibians and small mammals. They are not aggressive and when threatened, they tend to hide or escape in the water rather than bite.",
                    "Nerodia fasciata, commonly known as the banded water snake, is a species of nonvenomous snake found in the southeastern and central United States. They are typically between 2 and 4 feet in length, and are known for their heavy bodies and pattern of dark brown or black crossbands on a light-colored background. They are semi-aquatic and are often found near water, where they hunt for fish, amphibians and small mammals. They are not aggressive and when threatened, they tend to hide or escape in the water rather than bite. They are also known to be mistaken as venomous Cottonmouth (Agkistrodon piscivorus) which is a venomous snake.",
                    "Nerodia rhombifer, commonly known as the diamondback water snake, is a species of nonvenomous snake found in the southern United States. They are typically between 2 and 4 feet in length, and are known for their heavy bodies, smooth scales and a pattern of diamond-shaped dark brown or black crossbands on a light-colored background. They are semi-aquatic and are often found near water, where they hunt for fish, amphibians and small mammals. They are not aggressive and when threatened, they tend to hide or escape in the water rather than bite. They are also known to be mistaken as venomous Cottonmouth (Agkistrodon piscivorus) which is a venomous snake.",
                    "Nerodia sipedon, commonly known as the northern water snake, is a species of nonvenomous snake found in North America. They are typically between 2 and 4 feet in length, and are known for their heavy bodies, smooth scales and a pattern of brown or gray blotches on a light-colored background. They are semi-aquatic and are often found near water, where they hunt for fish, amphibians, and small mammals. They are not aggressive and when threatened, they tend to hide or escape in the water rather than bite. They are also known to be mistaken as venomous Cottonmouth (Agkistrodon piscivorus) which is a venomous snake.",
                    "Opheodrys aestivus, commonly known as the rough green snake, is a species of nonvenomous snake found in North America. They are typically between 2 and 4 feet in length, and are known for their bright green color and smooth scales. They are arboreal and are often found in trees and shrubs, where they feed on insects, spiders, and small lizards. They are not aggressive and when threatened, they tend to hide or escape rather than bite. They are known for their docile nature and are often kept as pets. They are also known to have a very docile temperament, making them an excellent choice for a pet snake for first-time snake owners.",
                    "Pantherophis alleghaniensis, commonly known as the eastern rat snake, is a species of nonvenomous snake found in the eastern United States. They are typically between 3 and 6 feet in length, and are known for their glossy black or dark gray color and smooth scales. They are terrestrial and are often found in wooded areas, where they feed on small mammals, birds, and reptiles. They are not aggressive and when threatened, they tend to hide or escape rather than bite. They are also known to be great climbers and are known to climb trees and buildings to catch prey or escape predators.",
                    "Pantherophis emoryi, commonly known as the Great Basin gopher snake or simply gopher snake, is a species of nonvenomous snake found in the western United States and northern Mexico. They are typically between 3 and 6 feet in length, and are known for their yellow, brown or gray color with dark blotches on the body. They are terrestrial and are often found in deserts and semi-deserts, where they feed on small mammals, such as gophers, ground squirrels and lizards. They are not aggressive and when threatened, they tend to hide or escape rather than bite. They are also known for their ability to mimic the movements and hiss of a rattlesnake, a behavior called caudal luring, to deter predators.",
                    "Pantherophis guttatus, commonly known as the corn snake, is a species of nonvenomous snake found in the southeastern United States. They are typically between 2 and 4 feet in length, and are known for their orange, red or brown color with dark blotches on the body. They are terrestrial and are often found in fields, forests and near human habitation, where they feed on small mammals, such as mice, rats and birds. They are not aggressive and when threatened, they tend to hide or escape rather than bite. They are also known to be docile and are often kept as pets. They are also known for their hardiness and ease of care, making them a popular choice for first-time snake owners.",
                    "Pantherophis obsoletus, commonly known as the black rat snake, is a species of nonvenomous snake found in the eastern and central United States, and parts of southern Canada. They are typically between 3 and 6 feet in length, and are known for their glossy black color and smooth scales. They are terrestrial and are often found in wooded areas, near human habitation, and near water, where they feed on small mammals, birds, and reptiles. They are not aggressive and when threatened, they tend to hide or escape rather than bite. They are also known to be great climbers and are known to climb trees and buildings to catch prey or escape predators. Due to their large size and ability to climb, they are also known to enter homes and buildings, which can cause conflicts with humans.",
                    "Pantherophis spiloides, commonly known as the gray rat snake or central rat snake, is a species of nonvenomous snake found in the central and eastern United States. They are typically between 3 and 6 feet in length, and are known for their gray or grayish-brown color and smooth scales. They are terrestrial and are often found in wooded areas, near human habitation, and near water, where they feed on small mammals, birds, and reptiles. They are not aggressive and when threatened, they tend to hide or escape rather than bite. They are also known to be great climbers and are known to climb trees and buildings to catch prey or escape predators. Due to their large size and ability to climb, they are also known to enter homes and buildings, which can cause conflicts with humans.",
                    "Pantherophis vulpinus, commonly known as the fox snake, is a species of nonvenomous snake found in the Midwest and Great Lakes regions of the United States and southern Ontario, Canada. They are typically between 2 and 4 feet in length, and are known for their yellow or tan color with brown or reddish-brown blotches on the body. They are terrestrial and are often found in fields and woodlands, where they feed on small mammals, such as rodents, and birds. They are not aggressive and when threatened, they tend to hide or escape rather than bite. They are also known for their defensive mimicry, when threatened, they tend to vibrate their tail, which can produce a hissing noise, similar to a rattle snake, to deter predators.",
                    "Pituophis catenifer, commonly known as the gopher snake or bull snake, is a species of nonvenomous snake found in North America. They are typically between 3 and 6 feet in length, and are known for their thick bodies and pattern of brown or gray blotches on a light-colored background. They are terrestrial and are often found in deserts, grasslands, and near human habitation, where they feed on small mammals, such as gophers and ground squirrels, and lizards. They are not aggressive and when threatened, they tend to hide or escape rather than bite. They are also known for their defensive mimicry, when threatened, they tend to flatten their head, raise their body, and vibrate their tail, which can produce a hissing noise, similar to a rattle snake, to deter predators.",
                    "Rhinocheilus lecontei, commonly known as the Long-nosed snake, is a species of nonvenomous snake found in the southwestern United States and northern Mexico. They are typically between 18 and 24 inches in length, and are known for their long, slender bodies, and long, pointed snout. They are typically gray, brown or olive in color, with a light-colored belly. They are found in a variety of habitats, from deserts to woodlands, where they feed on lizards, small mammals, and amphibians. They are not aggressive and when threatened, they tend to hide or escape rather than bite. They are also known for their ability to burrow and are often found underground.",
                    "Storeria dekayi, commonly known as the brown snake or DeKay's snake, is a species of nonvenomous snake found in the eastern United States and parts of southern Canada. They are typically between 10 and 14 inches in length, and are known for their brown or gray color and smooth scales. They are terrestrial and are often found in wooded areas, near human habitation, and near water, where they feed on small mammals, such as mice, and insects. They are not aggressive and when threatened, they tend to hide or escape rather than bite. They are also known for their docile nature and are often found in gardens, lawns and residential areas.",
                    "Storeria occipitomaculata, commonly known as the redbelly snake or northern red-bellied snake, is a species of nonvenomous snake found in the eastern United States and parts of southern Canada. They are typically between 8 and 15 inches in length, and are known for their brown or gray color and smooth scales, with a distinctive reddish or orange belly. They are terrestrial and are often found in wooded areas, near human habitation, and near water, where they feed on small mammals, such as mice, and insects. They are not aggressive and when threatened, they tend to hide or escape rather than bite. They are also known for their docile nature and are often found in gardens, lawns and residential areas.",
                    "Thamnophis elegans is a species of garter snake found in North America. They are known for their striking coloration, which can range from greenish-yellow to dark brown, and have three light-colored stripes running the length of their body. They are found in a variety of habitats, including woodlands, wetlands, and grasslands, and are known to be good swimmers. They are generally non-venomous and feed on a variety of prey, including fish, amphibians, and insects. They are also relatively small, reaching a maximum length of around 4 feet.",
                    "Thamnophis marcianus, also known as the western ribbon snake, is a species of garter snake that is native to the western United States and Mexico. They are known for their slender body and long tail, and their coloration can vary from light green to dark brown, with three yellowish or whitish stripes running the length of their body. They are found in a variety of habitats, including woodlands, wetlands, and grasslands, and are known to be good swimmers. They are generally non-venomous and feed on a variety of prey, including fish, amphibians, and insects. They are generally smaller than other garter snake species, reaching a maximum length of around 3 feet.",
                    "Thamnophis proximus, also known as the Western terrestrial garter snake, is a species of garter snake that is native to western North America. They have a slender body, and their coloration can vary from light green to dark brown, with three yellowish or whitish stripes running the length of their body. They are found in a variety of habitats, including woodlands, wetlands, and grasslands. They are generally non-venomous and feed on a variety of prey, including fish, amphibians, and insects. They are generally smaller than other garter snake species, reaching a maximum length of around 3 feet. They are known for their terrestrial habits, which makes them different from other garter snake species that are more aquatic or semi-aquatic.",
                    "Thamnophis radix, also known as the Plains garter snake, is a species of garter snake that is native to the central United States and Canada. They have a slender body and their coloration can vary from light green to dark brown, with three yellowish or whitish stripes running the length of their body. They are found in a variety of habitats, including woodlands, wetlands, and grasslands, and are known to be good swimmers. They are generally non-venomous and feed on a variety of prey, including fish, amphibians, and insects. They are generally smaller than other garter snake species, reaching a maximum length of around 3 feet. They are known for their terrestrial habits, which makes them different from other garter snake species that are more aquatic or semi-aquatic.",
                    "Thamnophis sirtalis, also known as the common garter snake, is a species of garter snake that is native to North America. They have a slender body, and their coloration can vary from greenish-yellow to dark brown, with three light-colored stripes running the length of their body. They are found in a variety of habitats, including woodlands, wetlands, and grasslands, and are known to be good swimmers. They are generally non-venomous and feed on a variety of prey, including fish, amphibians, and insects. They are one of the most wide spread snake species in North America and can reach lengths of up to 4 feet. They are also known to be adaptable to different environments, including urban and suburban areas.",
            };
            abc.setVisibility(View.VISIBLE);
            result.setVisibility(View.VISIBLE);
            confidencesText.setVisibility(View.VISIBLE);
            picture.setVisibility(View.INVISIBLE);
            aboutdata.setVisibility(View.VISIBLE);
            picture2.setVisibility(View.INVISIBLE);
            result.setText(classes[maxPos]);
            about.setText(Description[maxPos]);


            String s = "";
            for (int i = 0; i< classes.length;i++){
                s+= String.format("%s: %.1f%%\n",classes[i],confidences[i]*100);

            }

//            confidence.setText(s);
//            result.setText(classes[maxPos]);

//            confidence.setText(s);

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);

        } else if (resultCode == RESULT_OK && requestCode == 2) {
            Uri selectedImage = data.getData();
            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        // if (requestCode == 1 && resultCode == RESULT_OK) {
        //     Bitmap image = (Bitmap) data.getExtras().get("data");
        //     int dimension = Math.min(image.getWidth(),image.getHeight());
        //     image =ThumbnailUtils.extractThumbnail(image,dimension,dimension);
        //     imageView.setImageBitmap(image);

        //     image = Bitmap.createScaledBitmap(image,imageSize,imageSize,false);
        //     classifyImage(image);

        // }
        // else if (requestCode == 2 && resultCode == RESULT_OK  ){
        //     Bitmap image = (Bitmap) data.getExtras().get("data");
        //     int dimension = Math.min(image.getWidth(),image.getHeight());
        //     image =ThumbnailUtils.extractThumbnail(image,dimension,dimension);
        //     imageView.setImageBitmap(image);

        //     image = Bitmap.createScaledBitmap(image,imageSize,imageSize,false);
        //     classifyImage(image);
        // }

    }
}