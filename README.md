# Popular Movies

Here is a fully functional and colorful android app which I made from scratch for Android Developer Nanodegree program.

*Popular Movies* was highly evaluated by certified Udacity code reviewer and was graded as "Meets Specifications".

## Features

With the app, you can:
* Discover the most popular, the most rated or the highest rated movies
* Save favorite movies locally to view them even when offline
* Watch trailers
* Read reviews

## How to Work with the Source

This app uses [The Movie Database](https://www.themoviedb.org/documentation/api) API to retrieve movies.
You must provide your own API key in order to build the app. 

Add the following line to [USER_HOME]/.gradle/gradle.properties

For Windows OS, example for Akki user:

``` C:\Users\Akki\.gradle ```   

gradle.properties    

``` MyTheMovieDBApiToken="XXXXX" ```
    
## Screens

![screen](../master/art/phone-movies.png)

![screen](../master/art/phone-details.png)

## Libraries

* [ButterKnife](https://github.com/JakeWharton/butterknife)
* [Retrofit](https://github.com/square/retrofit)
* [OkHttp](https://github.com/square/okhttp)
* [Picasso](https://github.com/square/picasso)
* [Glide](https://github.com/bumptech/glide)
* [ExpandableTextView](https://github.com/Manabu-GT/ExpandableTextView)

## Android Developer Nanodegree
[![udacity][1]][2]

[1]: ../master/art/nanodegree-logo.png
[2]: https://www.udacity.com/course/android-developer-nanodegree--nd801

## License

    Copyright 2017 Akshay Jain

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
