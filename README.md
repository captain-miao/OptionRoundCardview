# OptionRoundCardView
Android CardView that allows you custom round corner position.

Base on Android support-v4 [CardView](https://android.googlesource.com/platform/frameworks/support.git) and [Slice](https://github.com/mthli/Slice)
Support Android 2.1+

## Screenshot

![option_round_card_view](https://raw.githubusercontent.com/captain-miao/me.github.com/master/cardview/option_round_card_view.gif "option_round_card_view")

## layout
```
    //rename attribute:cardCornerRadius replace optRoundCardCornerRadius
    <com.github.captain_miao.optroundcardview.OptRoundCardView
        android:id="@+id/top_card_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="@dimen/activity_horizontal_margin"
        android:layout_marginLeft="@dimen/activity_horizontal_margin"
        android:layout_marginRight="@dimen/activity_horizontal_margin"
        app:optRoundCardCornerRadius="8dp"
        app:optRoundCardLeftBottomCorner="false"
        app:optRoundCardRightBottomCorner="false">

        <TextView
            android:id="@+id/section_label"
            android:padding="@dimen/activity_horizontal_margin"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/>

    </com.github.captain_miao.optroundcardview.OptRoundCardView>

```


## API

 - `showCorner(boolean leftTop, boolean rightTop, boolean leftBottom, boolean rightBottom)`
 

 
## Gradle
Get library from  [oss.sonatype.org.io](https://oss.sonatype.org/content/repositories/snapshots)
```javascript

repositories {
    
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }

}

dependencies {
    compile 'com.github.captain-miao:optroundcardview:1.0.0-SNAPSHOT'
}
```

## Thanks

 - [Slice](https://github.com/mthli/Slice)
 - [CardView](https://android.googlesource.com/platform/frameworks/support.git)

## License

    Copyright (C) 2016 YanLu
    Copyright (C) 2016 Matthew Lee
    Copyright (C) 2014 The Android Open Source Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.