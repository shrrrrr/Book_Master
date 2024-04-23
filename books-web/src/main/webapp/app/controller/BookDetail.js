'use strict';

/**j
 * Login controller.
 */
App.controller('BookDetail', function($scope, $http, $location,$state) {
    // Initialize rating
    $scope.rating = 1;

    // Get book ID from URL parameters
    var bookId = $state.params.param1;
    console.log("This is the book id: "+bookId);
    // Fetch book details using the API
    $http.post('http://localhost:8080/books-web/api/common/getBook', { id: bookId }, {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
    .then(function(response) {
        $scope.book = response.data;
        console.log("Got the book baby:");
        console.log(response.data);
    })
    .catch(function(error) {
        console.error('Error fetching book details:', error);
    });

    // Function to submit the rating
    $scope.submitRating = function() {
        var formData = { book: bookId, rating: $scope.rating,user:"Shivansh" };

        // Call the API to submit the rating
        $http.put('http://localhost:8080/books-web/api/common/rate', formData, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        })
        .then(function(response) {
            console.log('Rating submitted successfully');
            window.location.reload();
            // Optionally, you can perform some action after submitting the rating
        })
        .catch(function(error) {
            console.error('Error submitting rating:', error);
        });
    };
});