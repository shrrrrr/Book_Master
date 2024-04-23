 // Spotify API Controller


 const SpotifyAPIController = (function() {
            
 })();
 
 // iTunes API Controller
 const iTunesAPIController = (function() {
     const baseURL = 'https://itunes.apple.com/search?';

     const constructURL = (searchQuery, country, entity) => {
         return `${baseURL}term=${encodeURIComponent(searchQuery)}&country=${country}&entity=${entity}`;
     }

     const searchContent = async (searchQuery, country, entity) => {
         const url = constructURL(searchQuery, country, entity);
         const response = await fetch(url);
         let data=await response.json();
        console.log(data);
         return data;
     
     }

     return {
         searchAudiobooks(searchQuery, country = 'us') {
             return searchContent(searchQuery, country, 'audiobook');
         },
         searchPodcasts(searchQuery, country = 'us') {
             return searchContent(searchQuery, country, 'podcast');
         }
     };
 })();

 // UI Controller
 const UIController = (function() {
     const DOMElements = {
         inputSearch: '#searchQuery',
         buttonSubmit: 'submit',
         divResults: '#results'
     }

     return {
         inputField() {
             return {
                 search: $(DOMElements.inputSearch),
                 submit: $(DOMElements.buttonSubmit),
                 results: $(DOMElements.divResults)
             }
         },

        createResult(name, description, imageUrl) {
const html = `
 <div>
     <h3>${name}</h3>
     <img src="${imageUrl}" alt="${name}">
     <p>${description}</p>
     <button class="addToFavouriteBtn">Add to Favourite</button>
 </div>
`;
$(DOMElements.divResults).append(html);

// Add click event listener to the newly created button
$('.addToFavouriteBtn').on('click', function() {
 addToFavourite(name); // Call a function to handle adding to favourites
});
},


         resetResults() {
             $(DOMElements.divResults).empty();
         }
     }
 })();

 // updated Spotify code---------------------------------------------------------------------------------------------------------------------
  const resultsDiv = document.getElementById("results");
  async function searchSpotify(query) {
     const clientId = '901bf219330b448fa73f9df5c1fe0e85';
const clientSecret = 'cebd98858a4243c0aa9b882a42c79821';
     const token = await getToken(clientId, clientSecret);

     if (!token) {
         alert("Failed to get access token.");
         return;
     }

     const contentType = document.getElementById("contentType").value;
     let type = '';
     if (contentType === 'audiobooks') {
         type = 'audiobook';
     } else if (contentType === 'podcasts') {
         type = 'show'; // 'show' represents podcasts in Spotify's API
     }

     const apiUrl = `https://api.spotify.com/v1/search?q=${query}&type=${type}`;
     const headers = {
         "Authorization": `Bearer ${token}`,
     };

     try {
         const response = await fetch(apiUrl, { headers });
         const data = await response.json();
         console.log(data);
         displayResults(data, contentType);
     } catch (error) {
         console.error("Error:", error);
     }
 }

 async function getToken(clientId, clientSecret) {
     const tokenUrl = "https://accounts.spotify.com/api/token";
     const data = `grant_type=client_credentials&client_id=${clientId}&client_secret=${clientSecret}`;

     try {
         const response = await fetch(tokenUrl, {
             method: "POST",
             headers: {
                 "Content-Type": "application/x-www-form-urlencoded",
             },
             body: data,
         });
         const tokenData = await response.json();
         return tokenData.access_token;
     } catch (error) {
         console.error("Error:", error);
         return null;
     }
 }

function displayResults(data, contentType) {
     // Clear previous results
     resultsDiv.innerHTML = "";
     const items = contentType === 'audiobooks' ? data.audiobooks.items : data.podcasts.items;

     if (items.length > 0) {
         // Create a header for the results
         const header = document.createElement("h2");
         header.textContent = contentType === 'audiobooks' ? 'Audiobooks:' : 'Podcasts:';
         resultsDiv.appendChild(header);

         // Create a grid to display the results
         const grid = document.createElement("div");
         grid.className = "results-grid";
         items.forEach(item => {
             console.log(item);
             console.log(item.id);
             const block = document.createElement("div");
             block.classList.add("result-item");

             // Set a specific width and height for the images
             const image = document.createElement("img");
             image.src = item.images[0].url;
             image.alt = item.name;
             image.style.width = "100px"; // Set the width
             image.style.height = "100px"; // Set the height

             const name = document.createElement("div");
             name.textContent = item.name;

             const authorName=document.createElement("div");
             authorName.textContent="Author: "  +  item.authors[0].name;
             

             // Create a "Add to Favourite" button

             //author Name : data[0].authors[0].name
             // name: data[0].name
             const addButton = document.createElement("button");
             addButton.textContent = "Add to Favourite";
             addButton.addEventListener("click", () => {
                 addToFavourite(item.name,item.authors[0].name) // Call a function to handle adding to favourites
             });

             // Append elements to the block
             block.appendChild(image);
             block.appendChild(name);
             block.appendChild(authorName);
             block.appendChild(addButton);


             // Append the block to the grid
             grid.appendChild(block);
         });

         // Append the grid to the results container
         resultsDiv.appendChild(grid);
     } else {
         // If no results found, display a message
         const noResultsMessage = document.createElement("p");
         noResultsMessage.textContent = `No ${contentType === 'audiobooks' ? 'audiobooks' : 'podcasts'} found.`;
         resultsDiv.appendChild(noResultsMessage);
     }
 }




 //--------------------------------------------------------------------------------------------------------------------------------











 // App Controller
 const AppController = (function(UICtrl, SpotifyCtrl, iTunesCtrl) {
     const DOMInputs = UICtrl.inputField();

     $(document).on('submit', '#audioForm', async function(event) {
         event.preventDefault();

         const provider = $('#audioProvider').val();
         const contentType = $('#contentType').val();
         const searchQuery = DOMInputs.search.val().trim();

         UICtrl.resetResults();

         if (provider === 'spotify') {
             
            // const token = await SpotifyCtrl.getToken();
            // const audiobooks = await SpotifyCtrl.searchAudiobooks(token, searchQuery);
             //audiobooks.forEach(audiobook => {
              //   UICtrl.createResult(audiobook.name, audiobook.description, audiobook.imageUrl);
                 

                 searchSpotify(searchQuery);
            // });
         } else if (provider === 'itunes') {
             if (contentType === 'audiobooks') {
                 const results = await iTunesCtrl.searchAudiobooks(searchQuery);

                 results.results.forEach(result => {
                     UICtrl.createResult(result.collectionName, result.artistName, result.artworkUrl100);
                     console.log(result);
                 });
             } else if (contentType === 'podcasts') {
                 const results = await iTunesCtrl.searchPodcasts(searchQuery);
                 results.results.forEach(result => {
                     UICtrl.createResult(result.collectionName, result.artistName, result.artworkUrl100);
                 });
             }
         }
     });

     return {
         init() {
             console.log('App is starting');
         }
     }
 })(UIController, SpotifyAPIController, iTunesAPIController);





 // Itunes i will send folowing details to local database
 // 1) artistName
 // 2) collectionId
 // 3) collectionName
//  4) Collection type podcast / audionbook

 // wrapperType: 'audiobook', artistId: 296898111, collectionId: 1442046673, artistName: 'Frank Herbert', collectionName: 'Dune',

 // Spotify i will send following to local databse 

 // author Name : data[0].authors[0].name
 // name: data[0].name
 // content id: data.id;
 // colllection type podcast /audio book
 



 // 



// POST request to server---------------------------------------------------------------------------------------------------------------------------------























function addToFavourite(itemname, author) {

     // Fetch the service provider and content type from the DOM
     const serviceProvider = $('#audioProvider').val();
     const contentType = $('#contentType').val();
     console.log(serviceProvider);
     console.log("hello i am here");
     console.log(contentType);

     // Make a POST request to your backend to add the item to favourites
     fetch('http://localhost:8080/books-web/api/favourite/favourites', {
         method: 'POST',
         headers: {
             'Content-Type': 'application/json'
         },
         body: JSON.stringify({
             serviceprovider: serviceProvider,
             contenttype: contentType,
             itemname: itemname,
             authorname: author
         })
     })
     .then(response => {
         if (!response.ok) {
             throw new Error('Failed to add item to favourites');
         }
         return response.json();
     })
     .then(data => {
         console.log(data);
         alert("Item added to favourites successfully!");
     })
     .catch(error => {
         console.error('Error:', error);
         alert("Failed to add item to favourites. Please try again later.");
     });

 
}

// Function to fetch item ID from the server based on its name
// Function to fetch item ID (audiobook) from Spotify API by its ID
async function fetchItemId(itemID, accessToken) {
try {
 // Construct the URL with the item ID
 const url = `https://api.spotify.com/v1/audiobooks/${itemID}`;

 // Construct the headers with the access token
 const headers = {
     'Authorization': `Bearer ${accessToken}`
 };

 // Make a GET request to the Spotify API endpoint
 const response = await fetch(url, { headers });
 if (!response.ok) {
     throw new Error('Failed to fetch item ID');
 }
 
 // Parse the response JSON
 const data = await response.json();

 // Extract the required information (author name and description) from the response
 const authors = data.authors.map(author => author.name).join(', '); // Concatenate multiple authors if exist
 const description = data.description;

 // Create an object containing the extracted data
 const itemDetails = {
     authors: authors,
     description: description
 };

 // Return the item details
 return itemDetails;
} catch (error) {
 console.error('Error:', error);
 return null; // Return null in case of error
}
}
//------------------------------------------------------------------------------------GET METHOD from Locacl Databse-----------------------------------------
let ele=document.getElementById("showfavourite");
ele.addEventListener("click",()=>{

 showfavourite();
})

async function showfavourite() {
const URL = 'http://localhost:8080/books-web/api/favourite/favourite';
try {
 const response = await fetch(URL);
 if (!response.ok) {
     throw new Error('Failed to fetch favourite');
 }
 const data = await response.json();
 console.log(data);

 // Clear previous results
 resultsDiv.innerHTML = "";

 // Check if there are any favorite items
 if (data.books.length > 0) { // Accessing the 'books' property
     // Loop through the favorite items and display them
     data.books.forEach(item => { // Accessing the 'books' property
         const block = document.createElement("div");
         block.classList.add("favorite-item");

         // Create elements to display item details
         const itemName = document.createElement("h3");
         itemName.textContent = item.itemname;

         const authorName = document.createElement("p");
         authorName.textContent = "Author: " + item.authorname;

         // Append elements to the block
         block.appendChild(itemName);
         block.appendChild(authorName);

         // Append the block to the results container
         resultsDiv.appendChild(block);
     });
 } else {
     // If there are no favorite items, display a message
     const noResultsMessage = document.createElement("p");
     noResultsMessage.textContent = "No favorite items found.";
     resultsDiv.appendChild(noResultsMessage);
 }
} catch (error) {
 console.error('Error', error);
}
}




 AppController.init();