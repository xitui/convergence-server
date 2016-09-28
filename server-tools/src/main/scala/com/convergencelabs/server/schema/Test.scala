package com.convergencelabs.server.schema

import org.json4s.DefaultFormats
import org.json4s.Extraction
import org.json4s.jackson.JsonMethods

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.json4s.ShortTypeHints
import org.json4s.ext.EnumNameSerializer

object Test {


  val deltaYaml = """version: 1
description: Initial Domain Schema Creation
changes:

########## DateTimeFormat ##########

  - type: RunSQLCommand
    command: "ALTER DATABASE DATETIMEFORMAT \"yyyy-MM-dd'T'HH:mm:ss.SSSZ\";"

########## ModelSnapshotConfig Class ##########

  - type: CreateClass
    name: ModelSnapshotConfig
    properties: 
      - name: ModelSnapshotConfig
        type: Boolean
        constraints:
          mandatory: true 
          notNull: true
          
      - name: triggerByVersion
        type: Boolean
        constraints:
          mandatory: true 
          notNull: true

      - name: limitedByVersion
        type: Boolean
        constraints:
          mandatory: true 
          notNull: true

      - name: minVersionInterval
        type: Long
        constraints:
          mandatory: true 
          notNull: true

      - name: maxVersionInterval
        type: Long
        constraints:
          mandatory: true 
          notNull: true

      - name: triggerByTime
        type: Boolean
        constraints:
          mandatory: true 
          notNull: true

      - name: limitedByTime
        type: Boolean
        constraints:
          mandatory: true 
          notNull: true

      - name: minTimeInterval
        type: Long
        constraints:
          mandatory: true 
          notNull: true

      - name: maxTimeInterval
        type: Long
        constraints:
          mandatory: true 
          notNull: true

########## TokenPublicKey Class ##########

  - type: CreateClass
    name: TokenPublicKey
    properties: 
      - name: id
        type: String
        constraints:
          mandatory: true 
          notNull: true
          
      - name: name
        type: String
        constraints:
          mandatory: true 
          notNull: true

      - name: description
        type: String
        constraints:
          mandatory: true 
          notNull: true

      - name: created
        type: DateTime
        constraints:
          mandatory: true 
          notNull: true

      - name: enabled
        type: Boolean
        constraints:
          mandatory: true 
          notNull: true

      - name: key
        type: String
        constraints:
          mandatory: true 
          notNull: true
          
  - type: CreateIndex
    className: TokenPublicKey
    name: TokenPublicKey.id
    indexType: UniqueHashIndex
    properties: [id]

########## DomainConfig Class ##########

  - type: CreateClass
    name: DomainConfig
    properties: 
      - name: modelSnapshotConfig
        type: Embedded
        classType: ModelSnapshotConfig
        constraints:
          mandatory: true 
          notNull: true

      - name: adminPublicKey
        type: String
        constraints:
          mandatory: true 
          notNull: true
          
      - name: adminPrivateKey
        type: String
        constraints:
          mandatory: true 
          notNull: true
          
########## Collection Class ##########

  - type: CreateClass
    name: Collection
    properties: 
      - name: collectionId
        type: String
        constraints:
          mandatory: true 
          notNull: true

      - name: name
        type: String
        constraints:
          mandatory: true 
          notNull: true
          
      - name: overrideSnapshotConfig
        type: Boolean
        constraints:
          mandatory: true 
          notNull: true
          default: false

      - name: snapshotConfig
        type: Embedded
        classType: ModelSnapshotConfig
        constraints:
          notNull: true

  - type: CreateIndex
    className: Collection
    name: Collection.collectionId
    indexType: UniqueHashIndex
    properties: [collectionId]

########## Model Class ##########

  - type: CreateClass
    name: Model
    properties: 
      - name: collectionId
        type: String
        constraints:
          mandatory: true 
          notNull: true
          
      - name: modelId
        type: String
        constraints:
          mandatory: true 
          notNull: true

      - name: version
        type: Long
        constraints:
          mandatory: true 
          notNull: true
          min: 0

      - name: createdTime
        type: DateTime
        constraints:
          mandatory: true 
          notNull: true
          readOnly: true

      - name: modifiedTime
        type: DateTime
        constraints:
          mandatory: true 
          notNull: true

  - type: CreateIndex
    className: Model
    name: Model.collectionId_modelId
    indexType: UniqueHashIndex
    properties: [collectionId, modelId]

########## ModelSnapshot Class ##########

  - type: CreateClass
    name: ModelSnapshot
    properties: 
      - name: collectionId
        type: String
        constraints:
          mandatory: true 
          notNull: true
          
      - name: modelId
        type: String
        constraints:
          mandatory: true 
          notNull: true

      - name: version
        type: Long
        constraints:
          mandatory: true 
          notNull: true
          min: 0

      - name: timestamp
        type: DateTime
        constraints:
          mandatory: true 
          notNull: true

  - type: CreateIndex
    className: ModelSnapshot
    name: ModelSnapshot.collectionId_modelId_version
    indexType: Unique
    properties: [collectionId, modelId, version]

  - type: CreateIndex
    className: ModelSnapshot
    name: ModelSnapshot.collectionId_modelId
    indexType: NotUniqueHashIndex
    properties: [collectionId, modelId]

########## DataValue Class ##########

  - type: CreateClass
    name: DataValue
    abstract: true
    properties: 
      - name: vid
        type: String
        constraints:
          mandatory: true 
          notNull: true
          readOnly: true

      - name: model
        type: Link
        classType: Model
        constraints:
          mandatory: true 
          notNull: true
          readOnly: true
    
########## ObjectValue Class ##########

  - type: CreateClass
    name: ObjectValue
    superclass: DataValue
    properties: 
      - name: children
        type: LinkMap
        classType: DataValue
        constraints:
          mandatory: true 
          notNull: true
          
########## ArrayValue Class ##########

  - type: CreateClass
    name: ArrayValue
    superclass: DataValue
    properties: 
      - name: children
        type: LinkList
        constraints:
          mandatory: true 
          notNull: true
    
########## StringValue Class ##########

  - type: CreateClass
    name: StringValue
    superclass: DataValue
    properties: 
      - name: value
        type: String
        constraints:
          mandatory: true 
          notNull: true
     
########## DoubleValue Class ##########

  - type: CreateClass
    name: DoubleValue
    superclass: DataValue
    properties: 
      - name: value
        type: Double
        constraints:
          mandatory: true
          notNull: true
          
########## BooleanValue Class ##########

  - type: CreateClass
    name: BooleanValue
    superclass: DataValue
    properties: 
      - name: value
        type: Boolean
        constraints:
          mandatory: true
          notNull: true

########## NullValue Class ##########

  - type: CreateClass
    name: NullValue
    superclass: DataValue

########## OpValue Class ##########

  - type: CreateClass
    name: OpValue
    abstract: true
    properties: 
      - name: vid
        type: String
        constraints:
          mandatory: true 
          notNull: true
          readOnly: true

########## ObjectOpValue Class ##########

  - type: CreateClass
    name: ObjectOpValue
    superclass: OpValue
    properties: 
      - name: children
        type: EmbeddedMap
        classType: OpValue
        constraints:
          mandatory: true 
          notNull: true
          readOnly: true

########## ArrayOpValue Class ##########

  - type: CreateClass
    name: ArrayOpValue
    superclass: OpValue
    properties: 
      - name: children
        type: EmbeddedList
        classType: OpValue
        constraints:
          mandatory: true 
          notNull: true
          readOnly: true

########## StringOpValue Class ##########

  - type: CreateClass
    name: StringOpValue
    superclass: OpValue
    properties: 
      - name: value
        type: String
        constraints:
          mandatory: true 
          notNull: true
          readOnly: true

########## DoubleOpValue Class ##########

  - type: CreateClass
    name: DoubleOpValue
    superclass: OpValue
    properties: 
      - name: value
        type: Double
        constraints:
          mandatory: true 
          notNull: true
          readOnly: true

########## BooleanOpValue Class ##########

  - type: CreateClass
    name: BooleanOpValue
    superclass: OpValue
    properties: 
      - name: value
        type: Boolean
        constraints:
          mandatory: true 
          notNull: true
          readOnly: true
          

########## NullOpValue Class ##########

  - type: CreateClass
    name: NullOpValue
    superclass: OpValue       
    
    
    
    
    
    
########## Operation Class ##########

  - type: CreateClass
    name: Operation
    abstract: true    
    
########## DiscreteOperation Class ##########

  - type: CreateClass
    name: DiscreteOperation
    superclass: Operation
    abstract: true
    properties: 
      - name: vid
        type: String
        constraints:
          mandatory: true 
          notNull: true
          readOnly: true

      - name: noOp
        type: Boolean
        constraints:
          default: false 
    
########## CompoundOperation Class ##########

  - type: CreateClass
    name: CompoundOperation
    superclass: Operation
    properties: 
      - name: ops
        type: EmbeddedList
        classType: DiscreteOperation
        constraints:
          mandatory: true 
          notNull: true

########## StringInsertOperation Class ##########

  - type: CreateClass
    name: StringInsertOperation
    superclass: DiscreteOperation
    properties: 
      - name: idx
        type: Integer
        constraints:
          mandatory: true 
          notNull: true    
    
      - name: val
        type: String
        constraints:
          mandatory: true 
          notNull: true     
    
########## StringRemoveOperation Class ##########

  - type: CreateClass
    name: StringRemoveOperation
    superclass: DiscreteOperation
    properties: 
      - name: idx
        type: Integer
        constraints:
          mandatory: true 
          notNull: true    

      - name: length
        type: Integer
        constraints:
          mandatory: true 
          notNull: true    
              
      - name: oldVal
        type: String
        constraints:
          mandatory: true 
    
########## StringSetOperation Class ##########

  - type: CreateClass
    name: StringSetOperation
    superclass: DiscreteOperation
    properties: 
      - name: val
        type: String
        constraints:
          mandatory: true 
             
      - name: oldVal
        type: String
        constraints:
          mandatory: true     
    
 ########## ObjectSetPropertyOperation Class ##########

  - type: CreateClass
    name: ObjectSetPropertyOperation
    superclass: DiscreteOperation
    properties: 
      - name: prop
        type: String
        constraints:
          mandatory: true
          notNull: true 
             
      - name: val
        type: Embedded
        classType: OpValue
        constraints:
          mandatory: true      
    
      - name: oldVal
        type: Embedded
        classType: OpValue
        constraints:
          mandatory: true      
    
########## ObjectAddPropertyOperation Class ##########

  - type: CreateClass
    name: ObjectAddPropertyOperation
    superclass: DiscreteOperation
    properties: 
      - name: prop
        type: String
        constraints:
          mandatory: true
          notNull: true 
             
      - name: val
        type: Embedded
        classType: OpValue
        constraints:
          mandatory: true      
      
########## ObjectRemovePropertyOperation Class ##########

  - type: CreateClass
    name: ObjectAddPropertyOperation
    superclass: DiscreteOperation
    properties: 
      - name: prop
        type: String
        constraints:
          mandatory: true
          notNull: true     
    
      - name: oldVal
        type: Embedded
        classType: OpValue
        constraints:
          mandatory: true       
    
########## ObjectSetOperation Class ##########

  - type: CreateClass
    name: ObjectSetOperation
    superclass: DiscreteOperation
    properties: 
      - name: val
        type: EmbeddedMap
        classType: OpValue
        constraints:
          mandatory: true
    
      - name: oldVal
        type: EmbeddedMap
        classType: OpValue
        constraints:
          mandatory: true       
    
########## NumberAddOperation Class ##########

  - type: CreateClass
    name: NumberAddOperation
    superclass: DiscreteOperation
    properties: 
      - name: val
        type: Any
        constraints:
          mandatory: true 
          notNull: true
             
########## NumberSetOperation Class ##########

  - type: CreateClass
    name: NumberSetOperation
    superclass: DiscreteOperation
    properties: 
      - name: val
        type: Any
        constraints:
          mandatory: true 
          notNull: true
             
      - name: oldVal
        type: Any
        constraints:
          mandatory: true     
    
########## ArrayInsertOperation Class ##########

  - type: CreateClass
    name: ArrayInsertOperation
    superclass: DiscreteOperation
    properties: 
      - name: idx
        type: Integer
        constraints:
          mandatory: true
          notNull: true 
             
      - name: val
        type: Embedded
        classType: OpValue
        constraints:
          mandatory: true      
    
########## ArrayRemoveOperation Class ##########

  - type: CreateClass
    name: ArrayRemoveOperation
    superclass: DiscreteOperation
    properties: 
      - name: idx
        type: Integer
        constraints:
          mandatory: true
          notNull: true 
             
      - name: oldVal
        type: Embedded
        classType: OpValue
        constraints:
          mandatory: true      
        
########## ArrayReplaceOperation Class ##########

  - type: CreateClass
    name: ArrayReplaceOperation
    superclass: DiscreteOperation
    properties: 
      - name: idx
        type: Integer
        constraints:
          mandatory: true
          notNull: true 

      - name: val
        type: Embedded
        classType: OpValue
        constraints:
          mandatory: true
             
      - name: oldVal
        type: Embedded
        classType: OpValue
        constraints:
          mandatory: true     
    
########## ArrayMoveOperation Class ##########

  - type: CreateClass
    name: ArrayMoveOperation
    superclass: DiscreteOperation
    properties: 
      - name: fromIdx
        type: Integer
        constraints:
          mandatory: true
          notNull: true 
   
      - name: toIdx
        type: Integer
        constraints:
          mandatory: true
          notNull: true     
    
########## ArraySetOperation Class ##########

  - type: CreateClass
    name: ArraySetOperation
    superclass: DiscreteOperation
    properties: 
    
      - name: val
        type: EmbeddedList
        classType: OpValue
        constraints:
          mandatory: true
          notNull: true
             
      - name: oldVal
        type: EmbeddedList
        classType: OpValue
        constraints:
          mandatory: true

########## ModelOperation Class ##########

  - type: CreateClass
    name: ModelOperation
    properties: 
      - name: collectionId
        type: String
        constraints:
          mandatory: true 
          notNull: true
          
      - name: modelId
        type: String
        constraints:
          mandatory: true 
          notNull: true

      - name: version
        type: Long
        constraints:
          mandatory: true 
          notNull: true
          min: 0

      - name: timestamp
        type: DateTime
        constraints:
          mandatory: true 
          notNull: true

      - name: username
        type: String
        constraints:
          mandatory: true 
          notNull: true

      - name: sid
        type: String
        constraints:
          mandatory: true 
          notNull: true

      - name: op
        type: Any
        constraints:
          mandatory: true 
          notNull: true

  - type: CreateIndex
    className: ModelOperation
    name: ModelOperation.collectionId_modelId_version
    indexType: Unique
    properties: [collectionId, modelId, version]

  - type: CreateIndex
    className: ModelOperation
    name: ModelOperation.collectionId_modelId
    indexType: NotUniqueHashIndex
    properties: [collectionId, modelId]


########## User Class ##########

  - type: CreateClass
    name: User
    properties: 
      - name: username
        type: String
        constraints:
          mandatory: true 
          notNull: true
          
      - name: email
        type: String

      - name: firstName
        type: String

      - name: lastName
        type: String
          
  - type: CreateIndex
    className: User
    name: User.username
    indexType: Unique
    properties: [username]
    
  - type: CreateIndex
    className: User
    name: User.email
    indexType: Unique
    properties: [email]
    
########## UserCredential Class ##########

  - type: CreateClass
    name: UserCredential
    properties: 
      - name: user
        type: Link
        classType: User
        constraints:
          mandatory: true 
          notNull: true
          
      - name: password
        type: String
        constraints:
          mandatory: true 
          notNull: true

  - type: CreateIndex
    className: UserCredential
    name: UserCredential.user
    indexType: Unique
    properties: [user]


########## Additional Properties ##########
  - type: AddProperty
    className: Model
    property: 
      name: data
      type: Link
      classType: ObjectValue
      constraints:
        mandatory: true 
      
  - type: AddProperty
    className: ModelSnapshot
    property: 
      name: data
      type: Embedded
      classType: ObjectOpValue
      constraints:
        mandatory: true 


########## Sequences ##########

  - type: CreateSequence
    name: sessionSeq
    sequenceType: Ordered

########## Functions ##########

  - type: CreateFunction
    name: arrayInsert
    idempotent: false
    language:  "javascript"
    parameters: ["array", "index", "value"]
    code: "var idx = parseInt(index);\narray.add(idx, value);\nreturn array;"
    
  - type: CreateFunction
    name: arrayRemove
    idempotent: false
    language:  "javascript"
    parameters: ["array", "index"]
    code: "var idx = parseInt(index);\narray.remove(idx);\nreturn array;"
    
  - type: CreateFunction
    name: arrayReplace
    idempotent: false
    language:  "javascript"
    parameters: ["array", "index", "value"]
    code: "var idx = parseInt(index);\narray.set(idx, value);\nreturn array;"    
    
  - type: CreateFunction
    name: arrayMove
    idempotent: false
    language:  "javascript"
    parameters: ["array", "fromIndex", "toIndex"]
    code: "var toIn = parseInt(toIndex);\nvar fromIn = parseInt(fromIndex);\narray.add(toIn, array.remove(fromIn));\nreturn array;"
    
"""
  
  

  val mapper = new ObjectMapper(new YAMLFactory())
  implicit val f = DefaultFormats.withTypeHintFieldName("type") +
    new SimpleNamePolymorphicSerializer[Change]("type", List(classOf[CreateClass], classOf[AlterClass], classOf[DropClass], 
        classOf[AddProperty], classOf[AlterProperty], classOf[DropProperty],
        classOf[CreateIndex], classOf[DropIndex],
        classOf[CreateSequence], classOf[DropSequence],
        classOf[RunSQLCommand],
        classOf[CreateFunction], classOf[AlterFunction], classOf[DropFunction])) +
    new EnumNameSerializer(OrientType) +
    new EnumNameSerializer(IndexType) +
    new EnumNameSerializer(SequenceType)

  def main(args: Array[String]): Unit = {
    println(parseYaml[Delta](deltaYaml))
  }

  def parseYaml[A](yaml: String)(implicit mf: Manifest[A]): A = {
    val jsonNode = mapper.readTree(yaml)
    val jValue = JsonMethods.fromJsonNode(jsonNode)
    println(jsonNode)
    Extraction.extract[A](jValue)
  }
}
