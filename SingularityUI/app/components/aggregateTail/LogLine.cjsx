
LogLine = React.createClass

  render: ->
    <div className="line #{if @props.highlighted then 'highlightLine'  else '' }">
      <a href="##{@props.offset}" className="offset-link" data-offset="#{@props.offset}" onClick={@props.highlight}>
        <span className="glyphicon glyphicon-link" data-offset="#{@props.offset}"></span>
      </a>
      <span>
        {@props.content}
      </span>
    </div>

module.exports = LogLine