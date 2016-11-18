# SpringRecyclerView

SpringRecyclerView is a RecyclerView with spring effect when being dragged or flinged to overScroll.

ListView Version : https://github.com/gjiazhe/SpringListView

## Screenshot

<img src="screenshots/1.gif" width="270">

<img src="screenshots/2.gif" width="270">

<img src="screenshots/3.gif" width="270">

## Include SpringRecyclerView to Your Project

With gradle:

```groovy
dependencies {
   compile 'com.gjiazhe:SpringRecyclerView:1.0'
}
```
## Use SpringRecyclerView in Layout Files

```xml
<com.gjiazhe.springrecyclerview.SpringRecyclerView
        android:id="@+id/spring_recycler_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:srv_enableSpringEffectWhenDrag="true"
        app:srv_enableSpringEffectWhenFling="true"
        app:srv_releaseBackAnimDuration="300"
        app:srv_flingBackAnimDuration="300" />
```

## Description of Attributes
|           Attributes            | Format  | Default |               Description                |
| :-----------------------------: | :-----: | :-----: | :--------------------------------------: |
| srv_enableSpringEffectWhenDrag  | boolean |  true   | Whether the spring effect is enabled when we drag the recyclerView to overScroll. |
| srv_enableSpringEffectWhenFling | boolean |  true   | Whether the spring effect is enabled when we fling the recyclerView to overScroll. |
|   srv_releaseBackAnimDuration   |   int   |   300   | Duration of the rebound animation after we release the recyclerView. In millisecond. |
|    srv_flingBackAnimDuration    |   int   |   300   | Duration of the rebound animation after we fling the recyclerView. In millisecond. |

You can set these attributes in the layout file, or in the java code:

```java
SpringRecyclerView springRecyclerView = (SpringRecyclerView)findViewById(R.id.spring_recycler_view);
springRecyclerView.setEnableSpringEffectWhenDrag(true);
springRecyclerView.setEnableSpringEffectWhenFling(true);
springRecyclerView.setReleaseBackAnimDuration(300);
springRecyclerView.setFlingBackAnimDuration(300);
```
## License

    MIT License

    Copyright (c) 2016 郭佳哲

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
