# ZXing Barcode Scanner Android library

This allows for you to directly embed ZXing into your application **properly**.

Please note: This does not contain enough (any more) to work as the normal ZXing application on it's own. Resources are missing and it will look crap. (The CaptureActivity and other stuff is to be removed when I get round to doing it).

## Setup

Once cloned, you will need to deploy it via Maven. This is done by running `mvn clean install`.

In your application add it to pom.xml:

        <dependency>
            <groupId>com.google.zxing</groupId>
            <artifactId>android-lib</artifactId>
            <version>3.1</version>
            <type>apklib</type>
        </dependency>

If someone is a really nice person, they could deploy it to Maven Central (i have no idea).

## Gradle support

Now, you can add zxing-lib-extended to your libraries directory and declare that into settings.gradle as

    include ':libraries:zxing-lib-extended'

and default build.gradle:

    compile project(':libraries:zxing-lib-extended')

## How to use

You should only require the use of classes (my code) locaed in namespace `zxing.library`.

Everything is modernized into a Fragment, so you can add it to your layout like so:

	<fragment class="zxing.library.ZXingFragment"
            android:id="@+id/scanner"
            android:layout_width="match_parent" android:layout_height="match_parent" />

And by accessing the Fragment instance itself you can use `setDecodeCallback()` to set a new callback to fall down onto.

Also, in the callback you will need to ask the fragment to start rescanning using `restartScanning()` or `restartScanningIn( <value-in-milliseconds> )`. Like so:

	final ZXingFragment xf = (ZXingFragment) getSupportFragmentManager().findFragmentById(R.id.scanner);
        xf.setDecodeCallback(new DecodeCallback(){

			@Override
			public void handleBarcode(Result result, Bitmap arg1, float arg2) {
				Toast.makeText(HelloAndroidActivity.this, result.getText(), Toast.LENGTH_LONG).show();
				xf.restartScanningIn(200);
			}
        	
        });

## TODO

* Remove un-needed code
* Deploy to Maven Central somehow
* Replace setDecodeCallback method to onAttach ZXingFragment
