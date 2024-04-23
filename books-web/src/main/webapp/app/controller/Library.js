// 'use strict';

// /**j
//  * Login controller.
//  */
// App.controller('Library', function($scope, $http,$state) {
//     // Function to fetch books from the API
//     $scope.filteredBooks = []; // Array to hold filtered books
//     $scope.selectedGenres = {};
//     $scope.getBooks = function() {
//         $http.get('http://localhost:8080/books-web/api/common/hello')
//         .then(function(response) {
//             $scope.books = response.data.books;
//             $scope.filteredBooks = $scope.books;
//             function populateGenres() {
//                 var genresSet = new Set(); // Using a set to avoid duplicates
//                 $scope.books.forEach(function(book) {
//                     if (book.genre) {
//                         genresSet.add(book.genre);
//                     }
//                 });
//                 $scope.genres = Array.from(genresSet);
//             }
        
//             // Initial call to populate genres
//             populateGenres();
//         })
//         .catch(function(error) {
//             console.error('Error fetching books:', error);
//         });
//     };

//     $scope.applyGenreFilter = function() {
//         $scope.filteredBooks = $scope.books.filter(function(book) {
//             if (!$scope.selectedGenres || Object.keys($scope.selectedGenres).length === 0) {
//                 return true; // If no genre selected, show all books
//             } else {
//                 return $scope.selectedGenres[book.genre];
//             }
//         });
//     };

//     // Call the function to fetch books when the controller is initialized
//     $scope.getBooks();
//     $scope.showBookDetail=function(id){
//         console.log("This is working baby!!");
//         $state.go("bookDetail",{'param1':id});
//     }
//     $scope.gotoAddBook=function(){
//         console.log("This is working baby!!");
//         $state.go("addBook");
//     }
// });

'use strict';

/**j
 * Login controller.
 */
// App.controller('Library', function($scope, $http, $state) {
//     // Function to fetch books from the API
//     $scope.filteredBooks = []; // Array to hold filtered books
//     $scope.selectedGenres = {};
//     $scope.selectedAuthors = {};

//     $scope.getBooks = function() {
//         $http.get('http://localhost:8080/books-web/api/common/hello')
//         .then(function(response) {
//             $scope.books = response.data.books;
//             $scope.filteredBooks = $scope.books;
//             function populateGenres() {
//                 var genresSet = new Set(); // Using a set to avoid duplicates
//                 var authorsSet = new Set(); // Using a set to avoid duplicates
//                 $scope.books.forEach(function(book) {
//                     if (book.genre) {
//                         genresSet.add(book.genre);
//                     }
//                     if (book.author) {
//                         authorsSet.add(book.author);
//                     }
//                 });
//                 $scope.genres = Array.from(genresSet);
//                 $scope.authors = Array.from(authorsSet);
//             }
        
//             // Initial call to populate genres
//             populateGenres();
//         })
//         .catch(function(error) {
//             console.error('Error fetching books:', error);
//         });
//     };

//     // $scope.applyGenreFilter = function() {
//     //     $scope.filteredBooks = $scope.books.filter(function(book) {
//     //         var genreFilterPassed = (!$scope.selectedGenres || Object.keys($scope.selectedGenres).length === 0) || $scope.selectedGenres[book.genre];
//     //         var authorFilterPassed = (!$scope.selectedAuthors || Object.keys($scope.selectedAuthors).length === 0) || $scope.selectedAuthors[book.author];
//     //         return genreFilterPassed && authorFilterPassed;
//     //     });
//     // };
    
//     // $scope.applyAuthorFilter = function() {
//     //     $scope.filteredBooks = $scope.books.filter(function(book) {
//     //         var genreFilterPassed = (!$scope.selectedGenres || Object.keys($scope.selectedGenres).length === 0) || $scope.selectedGenres[book.genre];
//     //         var authorFilterPassed = (!$scope.selectedAuthors || Object.keys($scope.selectedAuthors).length === 0) || $scope.selectedAuthors[book.author];
//     //         return genreFilterPassed && authorFilterPassed;
//     //     });
//     // };
//     $scope.applyFilters = function() {
//         $scope.filteredBooks = $scope.books.filter(function(book) {
//             // Filter by genre
//             var genreFilter = (!$scope.selectedGenres || Object.keys($scope.selectedGenres).length === 0) ||
//                              $scope.selectedGenres[book.genre];

//             // Filter by author
//             var authorFilter = (!$scope.selectedAuthors || Object.keys($scope.selectedAuthors).length === 0) ||
//                               $scope.selectedAuthors[book.author];

//             // Filter by rating
//             var ratingFilter = !$scope.selectedRating ||
//                                (book.rating && book.rating >= $scope.selectedRating);

//             return genreFilter && authorFilter && ratingFilter;
//         });
//     };

//     // Call the function to fetch books when the controller is initialized
//     $scope.getBooks();

//     $scope.showBookDetail = function(id) {
//         console.log("This is working baby!!");
//         $state.go("bookDetail", {'param1': id});
//     };

//     $scope.gotoAddBook = function() {
//         console.log("This is working baby!!");
//         $state.go("addBook");
//     };
// });

App.controller('Library', function($scope, $http, $state) {
    // Array to hold filtered books
    $scope.filteredBooks = [];
    // Object to hold selected genres
    $scope.selectedGenres = {};
    // Object to hold selected authors
    $scope.selectedAuthors = {};
    // Variable to hold selected rating
    $scope.selectedRating = '';
    // Variable to hold sorting criteria
    $scope.sortBy = 'rating';
    // Variable to indicate sorting order
    $scope.reverse = false;

    // Function to fetch books from the API
    $scope.getBooks = function() {
        $http.get('http://localhost:8080/books-web/api/common/hello')
        .then(function(response) {
            $scope.books = response.data.books;
            $scope.filteredBooks = $scope.books;
            function populateGenresAndAuthors() {
                var genresSet = new Set();
                var authorsSet = new Set();
                $scope.books.forEach(function(book) {
                    if (book.genre) {
                        genresSet.add(book.genre);
                    }
                    if (book.author) {
                        authorsSet.add(book.author);
                    }
                });
                $scope.genres = Array.from(genresSet);
                $scope.authors = Array.from(authorsSet);
            }
        
            // Initial call to populate genres and authors
            populateGenresAndAuthors();
        })
        .catch(function(error) {
            console.error('Error fetching books:', error);
        });
    };

    // Function to apply filters based on selected genres, authors, and rating
    $scope.applyFilters = function() {
        $scope.filteredBooks = $scope.books.filter(function(book) {
            // Filter by genre
            var genreFilter = (!$scope.selectedGenres || Object.keys($scope.selectedGenres).length === 0) ||
                             $scope.selectedGenres[book.genre];

            // Filter by author
            var authorFilter = (!$scope.selectedAuthors || Object.keys($scope.selectedAuthors).length === 0) ||
                              $scope.selectedAuthors[book.author];

            // Filter by rating
            var ratingFilter = !$scope.selectedRating ||
                               (book.rating && book.rating >= $scope.selectedRating);

            return genreFilter && authorFilter && ratingFilter;
        });
    };

    // Function to sort books based on the selected criteria
    $scope.sortBooks = function() {
        $scope.reverse = !$scope.reverse; // Toggle sorting order
        $scope.filteredBooks = $scope.filteredBooks.sort(function(a, b) {
            if ($scope.sortBy === 'rating') {
                return $scope.reverse ? b.rating - a.rating : a.rating - b.rating;
            } else if ($scope.sortBy === 'totalRatings') {
                return $scope.reverse ? b.totalRatings - a.totalRatings : a.totalRatings - b.totalRatings;
            }
        });
    };

    // Call the function to fetch books when the controller is initialized
    $scope.getBooks();

    // Function to show book details
    $scope.showBookDetail = function(id) {
        $state.go("bookDetail", {'param1': id});
    };

    // Function to navigate to add book page
    $scope.gotoAddBook = function() {
        $state.go("addBook");
    };
});

