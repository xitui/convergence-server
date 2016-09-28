package com.convergencelabs.server.schema

import org.json4s.DefaultFormats
import org.json4s.Extraction
import org.json4s.jackson.JsonMethods

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.json4s.ShortTypeHints
import org.json4s.ext.EnumNameSerializer

object Test {

  case class Person(firstName: String, lastName: String, age: Int)
  case class Group(name: String, members: List[Person])

  val groupYaml = """name: my group
members:
  - firstName: Michael
    lastName: MacFadden
    age: 36
  - firstName: Jim
    lastName: james
    age: 23
"""

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
        orientType: Boolean
        constraints:
          mandatory: true 
          notNull: true
          
      - name: triggerByVersion
        orientType: Boolean
        constraints:
          mandatory: true 
          notNull: true

      - name: limitedByVersion
        orientType: Boolean
        constraints:
          mandatory: true 
          notNull: true

      - name: minVersionInterval
        orientType: Long
        constraints:
          mandatory: true 
          notNull: true

      - name: maxVersionInterval
        orientType: Long
        constraints:
          mandatory: true 
          notNull: true

      - name: triggerByTime
        orientType: Boolean
        constraints:
          mandatory: true 
          notNull: true

      - name: limitedByTime
        orientType: Boolean
        constraints:
          mandatory: true 
          notNull: true

      - name: minTimeInterval
        orientType: Long
        constraints:
          mandatory: true 
          notNull: true

      - name: maxTimeInterval
        orientType: Long
        constraints:
          mandatory: true 
          notNull: true

########## TokenPublicKey Class ##########

  - type: CreateClass
    name: TokenPublicKey
    properties: 
      - name: id
        orientType: String
        constraints:
          mandatory: true 
          notNull: true
          
      - name: name
        orientType: String
        constraints:
          mandatory: true 
          notNull: true

      - name: description
        orientType: String
        constraints:
          mandatory: true 
          notNull: true

      - name: created
        orientType: DateTime
        constraints:
          mandatory: true 
          notNull: true

      - name: enabled
        orientType: Boolean
        constraints:
          mandatory: true 
          notNull: true

      - name: key
        orientType: String
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
        orientType: Embedded
        classType: ModelSnapshotConfig
        constraints:
          mandatory: true 
          notNull: true

      - name: adminPublicKey
        orientType: String
        constraints:
          mandatory: true 
          notNull: true
          
      - name: adminPrivateKey
        orientType: String
        constraints:
          mandatory: true 
          notNull: true
          
########## Collection Class ##########

  - type: CreateClass
    name: Collection
    properties: 
      - name: collectionId
        orientType: String
        constraints:
          mandatory: true 
          notNull: true

      - name: name
        orientType: String
        constraints:
          mandatory: true 
          notNull: true
          
      - name: overrideSnapshotConfig
        orientType: Boolean
        constraints:
          mandatory: true 
          notNull: true
          default: false

      - name: snapshotConfig
        orientType: Embedded
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
        orientType: String
        constraints:
          mandatory: true 
          notNull: true
          
      - name: modelId
        orientType: String
        constraints:
          mandatory: true 
          notNull: true

      - name: version
        orientType: Long
        constraints:
          mandatory: true 
          notNull: true
          min: 0

      - name: createdTime
        orientType: DateTime
        constraints:
          mandatory: true 
          notNull: true
          readOnly: true

      - name: modifiedTime
        orientType: DateTime
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
        orientType: String
        constraints:
          mandatory: true 
          notNull: true
          
      - name: modelId
        orientType: String
        constraints:
          mandatory: true 
          notNull: true

      - name: version
        orientType: Long
        constraints:
          mandatory: true 
          notNull: true
          min: 0

      - name: timestamp
        orientType: DateTime
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
        orientType: String
        constraints:
          mandatory: true 
          notNull: true
          readOnly: true

      - name: model
        orientType: Link
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
        orientType: LinkMap
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
        orientType: LinkList
        constraints:
          mandatory: true 
          notNull: true
    
########## StringValue Class ##########

  - type: CreateClass
    name: StringValue
    superclass: DataValue
    properties: 
      - name: value
        orientType: String
        constraints:
          mandatory: true 
          notNull: true
     
########## DoubleValue Class ##########

  - type: CreateClass
    name: DoubleValue
    superclass: DataValue
    properties: 
      - name: value
        orientType: Double
        constraints:
          mandatory: true
          notNull: true
          
########## BooleanValue Class ##########

  - type: CreateClass
    name: BooleanValue
    superclass: DataValue
    properties: 
      - name: value
        orientType: Boolean
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
        orientType: String
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
        orientType: EmbeddedMap
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
        orientType: EmbeddedList
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
        orientType: String
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
        orientType: Double
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
        orientType: Boolean
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
        orientType: String
        constraints:
          mandatory: true 
          notNull: true
          readOnly: true

      - name: noOp
        orientType: Boolean
        constraints:
          default: false 
    
########## CompoundOperation Class ##########

  - type: CreateClass
    name: CompoundOperation
    superclass: Operation
    properties: 
      - name: ops
        orientType: EmbeddedList
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
        orientType: Integer
        constraints:
          mandatory: true 
          notNull: true    
    
      - name: val
        orientType: String
        constraints:
          mandatory: true 
          notNull: true     
    
########## StringRemoveOperation Class ##########

  - type: CreateClass
    name: StringRemoveOperation
    superclass: DiscreteOperation
    properties: 
      - name: idx
        orientType: Integer
        constraints:
          mandatory: true 
          notNull: true    

      - name: length
        orientType: Integer
        constraints:
          mandatory: true 
          notNull: true    
              
      - name: oldVal
        orientType: String
        constraints:
          mandatory: true 
    
########## StringSetOperation Class ##########

  - type: CreateClass
    name: StringSetOperation
    superclass: DiscreteOperation
    properties: 
      - name: val
        orientType: String
        constraints:
          mandatory: true 
             
      - name: oldVal
        orientType: String
        constraints:
          mandatory: true     
    
 ########## ObjectSetPropertyOperation Class ##########

  - type: CreateClass
    name: ObjectSetPropertyOperation
    superclass: DiscreteOperation
    properties: 
      - name: prop
        orientType: String
        constraints:
          mandatory: true
          notNull: true 
             
      - name: val
        orientType: Embedded
        classType: OpValue
        constraints:
          mandatory: true      
    
      - name: oldVal
        orientType: Embedded
        classType: OpValue
        constraints:
          mandatory: true      
    
########## ObjectAddPropertyOperation Class ##########

  - type: CreateClass
    name: ObjectAddPropertyOperation
    superclass: DiscreteOperation
    properties: 
      - name: prop
        orientType: String
        constraints:
          mandatory: true
          notNull: true 
             
      - name: val
        orientType: Embedded
        classType: OpValue
        constraints:
          mandatory: true      
      
########## ObjectRemovePropertyOperation Class ##########

  - type: CreateClass
    name: ObjectAddPropertyOperation
    superclass: DiscreteOperation
    properties: 
      - name: prop
        orientType: String
        constraints:
          mandatory: true
          notNull: true     
    
      - name: oldVal
        orientType: Embedded
        classType: OpValue
        constraints:
          mandatory: true       
    
########## ObjectSetOperation Class ##########

  - type: CreateClass
    name: ObjectSetOperation
    superclass: DiscreteOperation
    properties: 
      - name: val
        orientType: EmbeddedMap
        classType: OpValue
        constraints:
          mandatory: true
    
      - name: oldVal
        orientType: EmbeddedMap
        classType: OpValue
        constraints:
          mandatory: true       
    
########## NumberAddOperation Class ##########

  - type: CreateClass
    name: NumberAddOperation
    superclass: DiscreteOperation
    properties: 
      - name: val
        orientType: Any
        constraints:
          mandatory: true 
          notNull: true
             
########## NumberSetOperation Class ##########

  - type: CreateClass
    name: NumberSetOperation
    superclass: DiscreteOperation
    properties: 
      - name: val
        orientType: Any
        constraints:
          mandatory: true 
          notNull: true
             
      - name: oldVal
        orientType: Any
        constraints:
          mandatory: true     
    
########## ArrayInsertOperation Class ##########

  - type: CreateClass
    name: ArrayInsertOperation
    superclass: DiscreteOperation
    properties: 
      - name: idx
        orientType: Integer
        constraints:
          mandatory: true
          notNull: true 
             
      - name: val
        orientType: Embedded
        classType: OpValue
        constraints:
          mandatory: true      
    
########## ArrayRemoveOperation Class ##########

  - type: CreateClass
    name: ArrayRemoveOperation
    superclass: DiscreteOperation
    properties: 
      - name: idx
        orientType: Integer
        constraints:
          mandatory: true
          notNull: true 
             
      - name: oldVal
        orientType: Embedded
        classType: OpValue
        constraints:
          mandatory: true      
        
########## ArrayReplaceOperation Class ##########

  - type: CreateClass
    name: ArrayReplaceOperation
    superclass: DiscreteOperation
    properties: 
      - name: idx
        orientType: Integer
        constraints:
          mandatory: true
          notNull: true 

      - name: val
        orientType: Embedded
        classType: OpValue
        constraints:
          mandatory: true
             
      - name: oldVal
        orientType: Embedded
        classType: OpValue
        constraints:
          mandatory: true     
    
########## ArrayMoveOperation Class ##########

  - type: CreateClass
    name: ArrayMoveOperation
    superclass: DiscreteOperation
    properties: 
      - name: fromIdx
        orientType: Integer
        constraints:
          mandatory: true
          notNull: true 
   
      - name: toIdx
        orientType: Integer
        constraints:
          mandatory: true
          notNull: true     
    
########## ArraySetOperation Class ##########

  - type: CreateClass
    name: ArraySetOperation
    superclass: DiscreteOperation
    properties: 
    
      - name: val
        orientType: EmbeddedList
        classType: OpValue
        constraints:
          mandatory: true
          notNull: true
             
      - name: oldVal
        orientType: EmbeddedList
        classType: OpValue
        constraints:
          mandatory: true

########## ModelOperation Class ##########

  - type: CreateClass
    name: ModelOperation
    properties: 
      - name: collectionId
        orientType: String
        constraints:
          mandatory: true 
          notNull: true
          
      - name: modelId
        orientType: String
        constraints:
          mandatory: true 
          notNull: true

      - name: version
        orientType: Long
        constraints:
          mandatory: true 
          notNull: true
          min: 0

      - name: timestamp
        orientType: DateTime
        constraints:
          mandatory: true 
          notNull: true

      - name: username
        orientType: String
        constraints:
          mandatory: true 
          notNull: true

      - name: sid
        orientType: String
        constraints:
          mandatory: true 
          notNull: true

      - name: op
        orientType: Any
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
        orientType: String
        constraints:
          mandatory: true 
          notNull: true
          
      - name: email
        orientType: String

      - name: firstName
        orientType: String

      - name: lastName
        orientType: String
          
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
        orientType: Link
        classType: User
        constraints:
          mandatory: true 
          notNull: true
          
      - name: password
        orientType: String
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
      orientType: Link
      classType: ObjectValue
      constraints:
        mandatory: true 
      
  - type: AddProperty
    className: ModelSnapshot
    property: 
      name: data
      orientType: Embedded
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
    ShortTypeHints(List(classOf[CreateClass], classOf[AlterClass], classOf[DropClass], 
        classOf[AddProperty], classOf[AlterProperty], classOf[DropProperty],
        classOf[CreateIndex], classOf[DropIndex],
        classOf[CreateSequence], classOf[DropSequence],
        classOf[RunSQLCommand],
        classOf[CreateFunction], classOf[AlterFunction], classOf[DropFunction])) +
    new EnumNameSerializer(OrientType) +
    new EnumNameSerializer(IndexType) +
    new EnumNameSerializer(SequenceType)

  def main(args: Array[String]): Unit = {
    println(parseYaml[Group](groupYaml))
    println(parseYaml[Delta](deltaYaml))
  }

  def parseYaml[A](yaml: String)(implicit mf: Manifest[A]): A = {
    val jsonNode = mapper.readTree(yaml)
    val jValue = JsonMethods.fromJsonNode(jsonNode)
    println(jsonNode)
    Extraction.extract[A](jValue)
  }
}