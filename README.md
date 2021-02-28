# Project 2 - *SimpleTweet*

**SimpleTweet** is an android app that allows a user to view their Twitter timeline. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: **30** hours spent in total

## User Stories

The following **required** functionality is completed:

- [x] User can **sign in to Twitter** using OAuth login
- [x]	User can **view tweets from their home timeline**
  - [x] User is displayed the username, name, and body for each tweet
  - [x] User is displayed the [relative timestamp](https://gist.github.com/nesquena/f786232f5ef72f6e10a7) for each tweet "8m", "7h"
- [x] User can refresh tweets timeline by pulling down to refresh

The following **optional** features are implemented:

- [x] User can view more tweets as they scroll with infinite pagination
- [x] Improve the user interface and theme the app to feel "twitter branded"
- [x] Links in tweets are clickable and will launch the web browser
---------------------------------------------------------------------
Detail View
- [ ] User can tap a tweet to display a "detailed" view of that tweet
- [ ] User can see embedded image media within the tweet detail view
---------------------------------------------------------------------
- [ ] User can watch embedded video within the tweet
- [ ] User can open the twitter app offline and see last loaded tweets
- [x] On the Twitter timeline, leverage the CoordinatorLayout to apply scrolling behavior that hides / shows the toolbar.

The following **additional** features are implemented:

- [x] Added heterogeneous layouts to the RecyclerView for Image & Text tweet
- [x] Added logout functionality to the Twitter icon in the stream screen
- [x] Toolbar hides/shows based on user scrolling

## Video Walkthrough

Here's a walkthrough of implemented user stories & stretch goals:

<img src='portrait_simpletweet.gif' title='Video Walkthrough' width="400" height="800" alt='Video Walkthrough' />

GIF created with [ScreenToGif](https://www.screentogif.com/).

## Notes

The most difficult part was creating heterogeneous layouts for the Stream RecyclerView. Initially
the concepts were a bit difficult however once I understood how the superclass Object played a role in 
adding the List of data I managed to add both layouts. My original issue was that I could not add an image and use the 
same layout for the different Tweets that did not have the attribute for the JSON data. This caused
an error when fetching at first then another issue with recycled item getting an image even though
they did not have that attribute in the JSON. Creating a heterogeneous layout in the RecyclerView
solved these issues.

Also, for the heterogeneous layouts I had to extend the Tweet for TweetImg which solved my issue
for Type Casting (Tweet) when getting the last item in the list since it could of been both. Extending
solved this issue since TweetImg is of Type Tweet now so I had to check the instanceOf TweetImg
first before checking instanceOf Tweet because of inheritance.

A challenge I experienced was with the RelativeLayout when the user had a long handle/username
causing the timer to overlay and make it illegible to read. What I did was add a constraint
from the handle TextView to the time TextView. This allowed the date to stay within the View
and cause the handle's overflow to hide if the handle/username was long.

The stretch infinite pagination also caused a few minor issues since the List of tweets was
not updating due to the max_id. However, this issue was fixed immediately by logging and debugging.

Overall, an amazing project with many new concepts learned!

## Open-source libraries used

- [Android Async HTTP](https://github.com/codepath/CPAsyncHttpClient) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android

## License

    Copyright 2021 Daniel Santana

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
