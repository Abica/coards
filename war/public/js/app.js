$( function() {
  $( ".delete a" ).click( function() {
    return confirm( "Are you sure you want to permanently delete this post and it's children?" );
  } );
} );
