function toggleOptions() {
  $( ".start-editing, .close-editing, .save-post" ).toggle();
}

$( function() {
  var app = $.sammy( function() {
    // dipsplay index
    this.get( "#/", function( context ) {

    } );

    // delete an object
    this.get( "#/delete", function( context ) {
      if ( confirm( "Are you sure you want to permanently delete this post and it's children?" ) ) {
        this.redirect( location.href.replace( /(\.html#.*?$)/, "/delete.html" ) );
      }
    } );

    // display edit form
    this.get( "#/edit", function( context ) {
      var editing_class = "editing";
      var title = $( ".breadcrumb:last" );
      var body = $( ".post-body" );

      if ( !title.hasClass( editing_class ) ) {
        toggleOptions();
        var title_text = title.text();
        var body_text = body.find( "pre" ).text();
        title.data( "original-text", title_text );
        body.data( "original-text", body_text );



        title.addClass( editing_class );
        title.html( $( '<input />' ).val( title_text ) );

        body.addClass( editing_class );
        var rows = body_text.split( "\n" ).length;
        body.html(
          $( '<textarea />' ).text( body_text ).attr( {
            rows: ( rows < 5 ) ? 5 : rows,
            cols: 40
          } )
        );
      }
    } );

    // save changed form
    this.get( "#/edit/save", function( context ) {
      var editing_class = "editing";
      var title = $( ".breadcrumb:last" );
      var body = $( ".post-body" );

      if ( title.hasClass( editing_class ) ) {
        var params = {
          title: title.find( "input:text" ).val(),
          body: body.find( "textarea" ).val()
        };

        title.data( "original-text", params.title );
        body.data( "original-text", params.body );

        this.redirect( "#/edit/cancel" );
      }
    } );

    // cancel editing mode and revert to previous state
    this.get( "#/edit/cancel", function( context ) {
      var editing_class = "editing";
      var title = $( ".breadcrumb:last" );
      var body = $( ".post-body" );

      if ( title.hasClass( editing_class ) ) {
        toggleOptions();
        title.html( title.data( "original-text" ) );
        body.html( $( "<pre />" ).text( body.data( "original-text" ) ) );

        title.removeClass( editing_class );
        body.removeClass( editing_class );

        this.redirect( "#" );
      }
    } );
  } );

  app.run( "#" );
} );
